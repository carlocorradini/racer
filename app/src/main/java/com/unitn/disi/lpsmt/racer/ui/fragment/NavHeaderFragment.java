package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.observer.UserObserver;

import java.util.Observable;
import java.util.Observer;

/**
 * Navigation Header Fragment
 *
 * @author Carlo Corradini
 */
public final class NavHeaderFragment implements Observer {

    /**
     * The header {@link View}
     */
    private View headerView;
    /**
     * {@link ImageView} {@link User} avatar
     */
    private ImageView imageAvatar;
    /**
     * {@link TextView} {@link User} username
     */
    private TextView txtUsername;
    /**
     * {@link TextView} {@link User} role
     */
    private TextView txtRole;
    /**
     * {@link UserObserver User Observer} {@link Observable}
     */
    private Observable userObservable;

    /**
     * Construct a {@link NavHeaderFragment} given the {@link View headerView} as root
     *
     * @param headerView The {@link View headerView} root
     * @throws NullPointerException If headerView is null
     */
    public NavHeaderFragment(final View headerView) throws NullPointerException {
        if (headerView == null) throw new NullPointerException("Navigation Header View is null");

        this.headerView = headerView;

        init();
    }

    /**
     * Initialize the {@link NavHeaderFragment} {@link View views} and {@link Observable observers}
     */
    private void init() {
        imageAvatar = headerView.findViewById(R.id.nav_header_avatar);
        txtUsername = headerView.findViewById(R.id.nav_header_username);
        txtRole = headerView.findViewById(R.id.nav_header_role);

        userObservable = UserObserver.getInstance();
        userObservable.addObserver(this);

        setUIData(UserObserver.getInstance().getUser());
    }

    /**
     * Set UI from the data given
     *
     * @param user The data {@link User}
     */
    private void setUIData(final User user) {
        if (user == null) return;
        Picasso.get().load(user.avatar.toString()).into(imageAvatar);
        txtUsername.setText(user.username);
        txtRole.setText(user.role.getValueRes());
    }

    /**
     * Method for logically destroy the {@link NavHeaderFragment} destroying all {@link Observable}
     */
    public void destroy() {
        userObservable.deleteObserver(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        userObservable.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UserObserver) {
            setUIData(((UserObserver) o).getUser());
        }
    }
}
