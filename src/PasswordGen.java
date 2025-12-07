import java.util.*;
import java.io.*;

// Main class
public class PasswordGen {

    // Function to check all password rules and return failures
    public static List<String> checkPassword(String password, List<String> userDetails) {
        List<String> fails = new ArrayList<>();

        // length check
        if (password.length() < 8)
            fails.add("Minimum 8 characters required");

        // uppercase check
        if (!password.matches(".*[A-Z].*"))
            fails.add("At least one uppercase letter required");

        // lowercase check
        if (!password.matches(".*[a-z].*"))
            fails.add("At least one lowercase letter required");

        // number check
        if (!password.matches(".*\\d.*"))
            fails.add("At least one digit required");

        // special character check
        if (!password.matches(".*[!@#$%^&*()_+\\-={}|\\[\\]:;\"'<>,.?/`~].*"))
            fails.add("At least one special character required");

        // Check if password contains personal info (unsafe practice)
        for (String detail : userDetails) {
            if (detail != null && !detail.trim().isEmpty()) {
                if (password.toLowerCase().contains(detail.toLowerCase())) {
                    fails.add("Password must NOT contain personal details (" + detail + ")");
                }
            }
        }
        return fails;
    }

    // Function to automatically generate a strong password
    public static String generateStrongPassword() {

        // different character sets
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-={}[]:;\"'<>,.?/";
        String all = upper + lower + digits + special;

        Random r = new Random();
        StringBuilder pass = new StringBuilder();

        // ensure at least 1 of each requirement
        pass.append(upper.charAt(r.nextInt(upper.length())));
        pass.append(lower.charAt(r.nextInt(lower.length())));
        pass.append(digits.charAt(r.nextInt(digits.length())));
        pass.append(special.charAt(r.nextInt(special.length())));

        // remaining random characters
        for (int i = 4; i < 12; i++) {
            pass.append(all.charAt(r.nextInt(all.length())));
        }

        // shuffle to make it unpredictable
        List<Character> chars = new ArrayList<>();
        for (char c : pass.toString().toCharArray()) chars.add(c);
        Collections.shuffle(chars);

        // rebuild shuffled string
        StringBuilder shuffled = new StringBuilder();
        for (char c : chars) shuffled.append(c);

        return shuffled.toString();
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("--------- PASSWORD STRENGTH CHECKER ---------");

        // user personal details
        System.out.print("Enter your Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter your Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Enter your Date of Birth (DD/MM/YYYY): ");
        String dob = sc.nextLine().trim();

        String spouse = "";
        String child = "";

        // optional personal info
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

        // password rules explanation
        System.out.println("\n------ PASSWORD RULES ------");
        System.out.println("Minimum 8 characters");
        System.out.println("At least 1 uppercase letter");
        System.out.println("At least 1 lowercase letter");
        System.out.println("At least 1 digit");
        System.out.println("At least 1 special character");
        System.out.println("Should NOT contain personal details\n");

        // password input
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        List<String> details = Arrays.asList(name, email, dob, spouse, child);

        // run validation
        List<String> fails = checkPassword(password, details);

        System.out.println("\n------ RESULT ------");

        // final result
        if (fails.isEmpty()) {
            System.out.println("Your password is STRONG.");
        } else {
            if (fails.size() <= 2)
                System.out.println("Password Strength: MEDIUM");
            else
                System.out.println("Password Strength: WEAK");

            System.out.println("\nIssues:");
            for (String f : fails)
                System.out.println("- " + f);

            // generate strong password suggestion
            System.out.print("\nGenerate strong password? (yes/no): ");
            String choose = sc.nextLine().trim().toLowerCase();

            if (choose.startsWith("y")) {
                System.out.println("Suggested Password: " + generateStrongPassword());
            }
        }

        System.out.println("\nThanks for using!");
        sc.close();
    }
}
