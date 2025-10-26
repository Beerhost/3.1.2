package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, PasswordEncoder paswordEncoder) {
        this.userService = userService;
        this.passwordEncoder = paswordEncoder;
    }


    @GetMapping
    public String adminPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @GetMapping("/users/list")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("newUser", new User());
        return "users";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "user-details";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute("newUser") User user,
                             @RequestParam(value = "roles", required = false) Set<Long> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "redirect:/admin/users/list";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        List<Role> allRoles = userService.findAllRoles();
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", userService.findAllRoles());
        Set<Long> userRoleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        model.addAttribute("userRoleIds", userRoleIds);
        return "edit-user";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(value = "password", required = false) String newPassword,
                             @RequestParam(value = "roles", required = false) Set<Long> roleIds) {

        User existingUser = userService.findById(id);
        existingUser.setUsername(username);
        existingUser.setEmail(email);

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }

        if (roleIds != null) {
            Set<Role> roles = roleIds.stream()
                    .map(userService::findRoleById)
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        } else {
            existingUser.setRoles(Set.of()); 
        }


        userService.save(existingUser);
        return "redirect:/admin/users/list";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users/list";
    }
}
