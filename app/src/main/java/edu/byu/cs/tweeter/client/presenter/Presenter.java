package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.model.service.AccountService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.view.BaseView;

public class Presenter {

    private BaseView view;
    private UserService userService;
    private AccountService accountService;

    public Presenter(BaseView view) {
        this.view = view;
        userService = new UserService();
        accountService = new AccountService();
    }

    public BaseView getView() {
        return view;
    }

    public UserService getUserService() {
        return userService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public abstract class Observer implements ServiceObserver {
        @Override
        public void handleFailure(String message) {
            view.displayMessage(getMessage() + ": " + message);
        }
        @Override
        public void handleException(Exception ex) {
            view.displayMessage(getMessage() + " because of exception: " + ex.getMessage());
        }
        protected abstract String getMessage();
    }
}
