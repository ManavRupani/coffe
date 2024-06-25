package test.coffee.lab.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import test.coffee.lab.config.JWTUtil;
import test.coffee.lab.entity.User;
import test.coffee.lab.repository.UserRepository;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            @RequestParam("type") String type) {
        // Create a new User object
        User user = new User(email, name, password, type);

        // Save the user to the database
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public String loginUser(User user, RedirectAttributes redirectAttributes) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null &&
                user.getPassword().equals(existingUser.getPassword())) {
            String token = jwtUtil.generateToken(existingUser.getEmail());
            redirectAttributes.addAttribute("token", token);
            if (existingUser.getType().equals("user")) {
                return "redirect:/user-detail";
            } else if (existingUser.getType().equals("admin")) {
                return "redirect:/admin-detail";
            }
        }
        return "redirect:/login?error";
    }

    @GetMapping("/user-detail")
    public String userDetail(Model model, HttpServletRequest request) {
        String token = request.getParameter("token");
        if (token != null && jwtUtil.validateToken(token)) {
            // You can add additional logic here if needed
            return "user-detail";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/admin-detail")
    public String adminDetail(Model model, HttpServletRequest request) {
        String token = request.getParameter("token");
        if (token != null && jwtUtil.validateToken(token)) {
            // You can add additional logic here if needed
            return "admin-detail";
        } else {
            return "redirect:/login";
        }
    }
}