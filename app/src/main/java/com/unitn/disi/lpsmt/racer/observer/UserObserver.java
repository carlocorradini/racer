package com.unitn.disi.lpsmt.racer.observer;

import android.os.CountDownTimer;
import android.util.Log;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link User User} {@link Observable Observer}
 *
 * @author Carlo Corradini
 */
public final class UserObserver extends Observable {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = UserObserver.class.getName();

    /**
     * Retry countdown in ms to wait before retry to call retrieveUser function
     */
    private static final long RETRY_COUNTDOWN_MS = 8000L;

    /**
     * Instance of the current {@link UserObserver} class assigned when the first {@link UserObserver#getInstance()} is called
     */
    private static UserObserver instance = null;

    /**
     * Current {@link User}
     */
    private User user;

    private UserObserver() {
        retrieveUser();
    }

    /**
     * Return the current {@link UserObserver} class instance.
     * The instance is constructed when this is the first call
     *
     * @return The {@link UserObserver} instance
     */
    public static UserObserver getInstance() {
        if (instance == null) {
            synchronized (UserObserver.class) {
                if (instance == null) {
                    instance = new UserObserver();
                }
            }
        }
        return instance;
    }

    /**
     * Set the {@link User user} to the given user.
     * After that, setChanged and notifyObservers method will be called
     *
     * @param user The new {@link User user} to set to
     */
    public void setUser(User user) {
        this.user = user;
        setChanged();
        notifyObservers();
    }

    /**
     * Return the current {@link User user}.
     * The returned value can be null.
     *
     * @return Current {@link User}, null otherwise
     */
    public User getUser() {
        return user;
    }

    /**
     * Refresh the current {@link User user} with a new one from the Server
     */
    public void refreshUser() {
        retrieveUser();
    }

    /**
     * Retrieve the current authenticated {@link User user} from the Server
     * and if the response is successfully and the body is not empty set the user
     * to the new value.
     * If the response is unsuccessful retry.
     */
    private void retrieveUser() {
        API.getInstance().getClient().create(UserService.class).me().enqueue(new Callback<API.Response<User>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<User>> call, @NotNull Response<API.Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successfully downloaded current authenticated user, notify observers");
                    setUser(response.body().data);
                } else {
                    Log.e(TAG, "Unable to download current authenticated user due to unsuccessful response");
                    retryRetrieveUser();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<User>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to download current authenticated user due to " + t.getMessage(), t);
                retryRetrieveUser();
            }
        });
    }

    /**
     * Retry to retrieveUser after waiting RETRY_COUNTDOWN_MS
     */
    private void retryRetrieveUser() {
        Log.i(TAG, "Retry to retrieve user after timeout");
        new CountDownTimer(RETRY_COUNTDOWN_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                retrieveUser();
            }
        }.start();
    }
}
