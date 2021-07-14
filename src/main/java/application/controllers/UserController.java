package application.controllers;

import application.models.User;
import application.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public String index(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return String.format("redirect:/profile/%s", principal.getName());
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile/{login}")
    public String user(@PathVariable("login") String log, Model model) {
        String logAuth = SecurityContextHolder.getContext().getAuthentication().getName();

        if (log.equals(logAuth)) {
            model.addAttribute("user", service.getUserByLogin(log));
            model.addAttribute("userAuth", service.getUserByLogin(logAuth));
            return "user";
        } else if (service.getUserByLogin(logAuth).isAdmin()) {
            model.addAttribute("user", service.getUserByLogin(log));
            model.addAttribute("userAuth", service.getUserByLogin(logAuth));
            return "user";
        }

        return String.format("redirect:/profile/%s", logAuth);
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("all_users", service.getAllUsers());
        return "admin";
    }


    @GetMapping("/admin/create")
    public String create(Model model) {
        model.addAttribute("new_user", new User());
        return "create";
    }

    @PostMapping("/admin/create")
    public String create(@ModelAttribute("new_user") User u, @RequestParam("new_user_roles") String rol) {
        u.setRoles(service.getRolesFromText(rol));
        service.addUser(u);
        return "redirect:/admin";
    }

    @GetMapping("/admin/update")
    public String update() {
        return "update";
    }

    @PatchMapping("/admin/update")
    public String update(@RequestParam("update_id") long id,
                         @RequestParam("update_log") String log,
                         @RequestParam("update_pas") String pas,
                         @RequestParam("update_rol") String rol,
                         @RequestParam("update_fn") String fn,
                         @RequestParam("update_sn") String sn,
                         @RequestParam("update_c") String c) {
        service.updateUser(id, log, pas, rol, fn, sn, c);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete")
    public String delete() {
        return "delete";
    }

    @DeleteMapping("/admin/delete")
    public String delete(@RequestParam("delete_id") Long id) {
        service.deleteUser(id);
        return "redirect:/admin";
    }
}