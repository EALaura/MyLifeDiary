package com.sya.mylifediary.Model;

/* Es el modelo para los uusarios que se registren en la aplicación
   cada usuario nuevo tendrá un nombre, un email, un usuario y una
   contraseña */
public class User {
    private String username;
    private String name;
    private String email;
    private String password1;
    private String password2;

    // Métodos get y set
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
