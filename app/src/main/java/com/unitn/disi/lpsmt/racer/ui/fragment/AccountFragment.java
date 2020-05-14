package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;

import org.jetbrains.annotations.NotNull;

import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.observer.UserObserver;

import java.util.Observable;
import java.util.Observer;

/**
 * {@link User} account {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class AccountFragment extends Fragment implements Observer {

    /**
     * {@link TextView} {@link User} username
     */
    private TextView txtUsername;
    /**
     * {@link ImageView} {@link User} avatar
     */
    private ImageView imageAvatar;
    /**
     * {@link UserObserver User Observer} {@link Observable}
     */
    private Observable userObservable;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        txtUsername = root.findViewById(R.id.fragment_account_username);
        imageAvatar = root.findViewById(R.id.fragment_account_avatar);

        userObservable = UserObserver.getInstance();
        userObservable.addObserver(this);

        setUIData(UserObserver.getInstance().getUser());

        return root;
    }

    /**
     * Set UI from the data given
     *
     * @param user The data {@link User}
     */
    private void setUIData(final User user) {
        if (user == null) return;
        txtUsername.setText(user.username);
        Picasso.get().load(user.avatar.toString()).into(imageAvatar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userObservable.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UserObserver) {
            User user = ((UserObserver) o).getUser();
            setUIData(user);
        }
    }
}
