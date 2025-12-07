import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

// Service interface
interface PasswordValidatorService {
    String validate(String password);
}

// Template validator
abstract class BasePasswordValidator implements PasswordValidatorService {
    abstract boolean isStrong(String password);

    @Override
    public String validate(String password) {
        return isStrong(password) ? "Valid" : "Invalid";
    }
}

// Simple length-based check
class BasicPasswordValidator extends BasePasswordValidator {
    @Override
    boolean isStrong(String password) {
        return password.length() >= 8;
    }
}

// Custom exception
class WeakPasswordException extends Exception {
    public WeakPasswordException(String msg) {
        super(msg);
    }
}

// Generic demonstration only
class GenericUtil {
    public static <T> void use(T value) {
        T temp = value;
    }
}

public class PasswordApp {

    // Rule-based checks
    public static List<String> checkPassword(String password, List<String> userDetails) {
        List<String> fails = new ArrayList<>();

        if (password.length() < 8)
            fails.add("Minimum 8 characters required");
        if (!password.matches(".*[A-Z].*"))
            fails.add("At least one uppercase letter required");
        if (!password.matches(".*[a-z].*"))
            fails.add("At least one lowercase letter required");
        if (!password.matches(".*\\d.*"))
            fails.add("At least one digit required");
        if (!password.matches(".*[!@#$%^&*()_+\\-={}|\\[\\]:;\"'<>,.?/`~].*"))
            fails.add("At least one special character required");

        // Avoid personal data inside password
        for (String detail : userDetails) {
            if (detail != null && !detail.trim().isEmpty()) {
                if (password.toLowerCase().contains(detail.toLowerCase())) {
                    fails.add("Password must NOT contain personal details (" + detail + ")");
                }
            }
        }
        return fails;
    }

    // Random generator
    public static String generateStrongPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-={}[]:;\"'<>,.?/";
        String all = upper + lower + digits + special;

        Random r = new Random();
        StringBuilder pass = new StringBuilder();

        // Guarantee one of each
        pass.append(upper.charAt(r.nextInt(upper.length())));
        pass.append(lower.charAt(r.nextInt(lower.length())));
        pass.append(digits.charAt(r.nextInt(digits.length())));
        pass.append(special.charAt(r.nextInt(special.length())));

        // Add rest
        for (int i = 4; i < 12; i++) {
            pass.append(all.charAt(r.nextInt(all.length())));
        }

        // Shuffle for randomness
        List<Character> chars = new ArrayList<>();
        for (char c : pass.toString().toCharArray()) chars.add(c);
        Collections.shuffle(chars);

        StringBuilder shuffled = new StringBuilder();
        for (char c : chars) shuffled.append(c);

        return shuffled.toString();
    }

    // Secure input (console / GUI)
    public static String readPassword(String prompt, Scanner scanner) {
        Console console = System.console();

        if (console != null) {
            return new String(console.readPassword(prompt));
        }

        try {
            JPasswordField pf = new JPasswordField();
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(prompt), BorderLayout.NORTH);
            panel.add(pf, BorderLayout.CENTER);

            JOptionPane.showConfirmDialog(null, panel, "Enter Password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            return new String(pf.getPassword());
        } catch (HeadlessException e) {
            System.out.print(prompt);
            return scanner.nextLine();
        }
    }
   //main
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--------- PASSWORD STRENGTH CHECKER & GENERATOR ---------");

        System.out.print("Enter your Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter your Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Enter your Date of Birth: ");
        String dob = sc.nextLine().trim();

        String spouse = "";
        String child = "";

        System.out.print("Are you married? (yes/no): ");
        String married = sc.nextLine().trim().toLowerCase();

        if (married.equals("yes") || married.equals("y")) {
            System.out.print("Enter your Spouse's Name: ");
            spouse = sc.nextLine().trim();

            System.out.print("Do you have children? (yes/no): ");
            String children = sc.nextLine().trim().toLowerCase();

            if (children.equals("yes") || children.equals("y")) {
                System.out.print("Enter Child's Name: ");
                child = sc.nextLine().trim();
            }
        }

        // Rules
        System.out.println("\n------ PASSWORD RULES ------");

        String password = readPassword("Enter your Password: ", sc);

        List<String> details = Arrays.asList(name, email, dob, spouse, child);
        List<String> fails = checkPassword(password, details);

        PasswordValidatorService validator = new BasicPasswordValidator();
        String check = validator.validate(password);

        // store failures and results
        Set<String> failSet = new HashSet<>(fails);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("status", check);

        try {
            if (!fails.isEmpty())
                throw new WeakPasswordException("Weak Password");
        } catch (WeakPasswordException e) {
        }

        // File logging
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("hidden_log.txt", true))) {
            bw.write("Status=" + check + "\n");
        } catch (IOException e) {}

        System.out.println("\n------ PASSWORD RESULT ------");

        if (fails.isEmpty()) {
            System.out.println("Your password is STRONG.");
        } else {
            if (fails.size() <= 2)
                System.out.println("Password Strength: MEDIUM");
            else
                System.out.println("Password Strength: WEAK");

            System.out.println("\nYour password failed:");
            for (String f : fails) System.out.println("- " + f);

            System.out.print("\nWant a suggested strong password? (yes/no): ");
            String choose = sc.nextLine().trim().toLowerCase();

            if (choose.equals("yes") || choose.equals("y")) {
                System.out.println(generateStrongPassword());
            }
        }
        sc.close();
    }
}
