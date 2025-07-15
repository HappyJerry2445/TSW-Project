package com.cardhaven.cardhaven.util;

import com.cardhaven.cardhaven.model.dto.UserDTO;
import jakarta.servlet.http.HttpSession;

public class UserLoginUtil {
    static public void login(HttpSession session, UserDTO user) {
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
        //session.setAttribute("userRole", user.getRole());
    }
}
