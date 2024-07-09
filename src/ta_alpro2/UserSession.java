/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ta_alpro2;

/**
 *
 * @author 3330
 */
public class UserSession {

    private static UserSession instance;
    private String username;
    private String role;

    private UserSession(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public static UserSession getInstance(String username, String role) {
        if (instance == null) {
            instance = new UserSession(username, role);
        }
        return instance;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void clearSession() {
        instance = null;
    }
}
