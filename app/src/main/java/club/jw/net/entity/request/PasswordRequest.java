package club.jw.net.entity.request;

import club.jw.net.anno.RequestParam;

public class PasswordRequest extends Request{
    @RequestParam("oldPassword")
    private final String oldPassword;

    @RequestParam("password")
    private final String password;

    @RequestParam("repeatedPassword")
    private final String repeatedPassword;

    @RequestParam("mail")
    private final String mail;

    @RequestParam("user.id")
    private final String userId;

    public PasswordRequest(String oldPassword, String password, String repeatedPassword, String mail, String userId) {
        this.oldPassword = oldPassword;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.mail = mail;
        this.userId = userId;
    }
}
