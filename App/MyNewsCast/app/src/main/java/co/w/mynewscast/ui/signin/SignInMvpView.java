package co.w.mynewscast.ui.signin;

import com.google.firebase.auth.FirebaseUser;

import co.w.mynewscast.ui.base.MvpView;

public interface SignInMvpView extends MvpView {

    void updateUI(FirebaseUser user);

    void revokeAccess();

    void signIn();

    void signOut();
}
