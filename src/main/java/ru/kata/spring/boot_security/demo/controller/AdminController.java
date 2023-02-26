package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;
import ru.kata.spring.boot_security.demo.models.User;


import javax.validation.Valid;

@Controller
public class AdminController {
    private final UserService userService;
    private final UserValidator userValidator;

    private final RoleService roleService;


    @Autowired
    public AdminController(UserService userService, UserValidator userValidator, RoleService roleService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.roleService = roleService;
    }

    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/admin/registration")
    public String registrationPage(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("listRoles", roleService.getUserRoles());
        return "admin/registration";
    }

    @PostMapping("/admin/registration")
    public String perfomRegistration(@ModelAttribute("userAdd") @Valid User user
            , BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "redirect:/users";
        }
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String getUsers(Model model,@ModelAttribute("user") @Valid User user
            , BindingResult bindingResult  ) {
        User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("person", userService.findUserByUsername(auth.getUsername()));
        model.addAttribute("listRoles", roleService.getUserRoles());
        return "users";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("userEdit", userService.findUserById(id));
        return "admin/edit";
    }

    @PatchMapping("/admin/{id}")
    public String update(@ModelAttribute("user") User user
            , @PathVariable("id") Long id) {
        userService.updateUser(id, user);
        return "redirect:/users";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}