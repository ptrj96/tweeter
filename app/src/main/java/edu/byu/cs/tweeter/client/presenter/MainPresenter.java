package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.AccountService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements AccountService.LogoutObserver {
    private MainActivityView view;
    private User user;
    private AuthToken token;

    public MainPresenter(User user, AuthToken token, MainActivityView view) {
        this.user = user;
        this.token = token;
        this.view = view;
    }

    public interface MainActivityView {
        void displayMessage(String message);
        void logoutUser();
    }

    @Override
    public void handleLogoutSuccess() {
        view.logoutUser();
    }

    @Override
    public void handleLogoutFailure(String message) {
        view.displayMessage("Failed to logout: " + message);
    }

    @Override
    public void handleLogoutThrewException(Exception ex) {
        view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
    }

    public void logout(){
        view.displayMessage("Logging Out...");
        new AccountService().logout(token, this);
    }

}
