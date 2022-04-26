package contacts;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

class Contact {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    static class Builder {
        private String firstName;
        private String lastName;
        private String phoneNumber;

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Contact build(){
            return new Contact(firstName, lastName, phoneNumber);
        }

    }

    Contact(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        if (!checkPhoneNumber()) {
            this.phoneNumber = "[no number]";
            System.out.println("Wrong number format!");
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean hasNumber(){
        return !phoneNumber.equals("[no number]");
    }

    private boolean checkPhoneNumber() {
        String firstGroup = "\\+?[0-9a-zA-Z]";
        String otherGroup = "([0-9a-zA-Z]{2,})";

        String regexp = "("+ firstGroup + "([ |-]" + otherGroup + "*)" + "|"
                + "\\(" + firstGroup + "\\)" + "([ |-]" + otherGroup + "*)" + "|" +
                firstGroup + "[ |-]" + "\\(" + otherGroup + "\\)" + "([ |-]" + otherGroup + "*)" +
                ")";
        return phoneNumber.matches(regexp);
    }

    @Override
    public String toString(){
        return getFirstName() + " " + getLastName() + ", " + getPhoneNumber();
    }

}

class PhoneBook {
    ArrayList<Contact> contacts;
    PhoneBook(){
        contacts = new ArrayList<>();
    }

    public void addContact(Scanner input) {
        System.out.println("Enter the name: ");
        Contact c = new Contact.Builder()
                .setFirstName(input.nextLine())
                .build();
        System.out.println("Enter the surname: ");
        c.setLastName(input.nextLine());
        System.out.println("Enter the number: ");
        c.setPhoneNumber(input.nextLine());
        contacts.add(c);
        System.out.println("The record added.");
    }

    public void listContacts() {
        int i = 0;
        for (Contact c : contacts) {
            System.out.println(i + ". " + c.toString());
        }
    }

    public void remove(Scanner input) {
        if (contacts.size() == 0) {
            System.out.println("No records to remove!");
            return;
        }
        System.out.println("Select a record: ");
        try {
            int i = input.nextInt();
            contacts.remove(i);
            System.out.println("The record removed!");
        } catch (IndexOutOfBoundsException | InputMismatchException e) {
            System.out.println("No such record.");
        }
    }

    public void edit(Scanner input) {
        if (contacts.size() == 0) {
            System.out.println("No records to edit!");
            return;
        }
        System.out.println("Select a record: ");
        try {
            int i = input.nextInt();
            System.out.println("Select a field (name, surname, number): ");
            String field = input.nextLine();
            switch (field) {
                case "name":
                    System.out.println("Enter name: ");
                    contacts.get(i).setFirstName(field);
                    break;
                case "surname":
                    System.out.println("Enter surname: ");
                    contacts.get(i).setLastName(field);
                    break;
                case "number":
                    System.out.println("Enter number: ");
                    contacts.get(i).setPhoneNumber(field);
                    break;
            }
            System.out.println("The record updated!!");
        } catch (IndexOutOfBoundsException | InputMismatchException e) {
            System.out.println("No such record.");
        }
    }

    public void count() {
        System.out.println("The Phone Book has "+ contacts.size() + " records.");
    }
}

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        PhoneBook pb = new PhoneBook();

        String action = "";
        while (!action.equals("exit")) {
            System.out.print("Enter action (add, remove, edit, count, list, exit): ");
            action = scanner.nextLine();
            switch (action) {
                case "add": pb.addContact(scanner); break;
                case "remove": pb.remove(scanner); break;
                case "edit": pb.edit(scanner); break;
                case "count": pb.count(); break;
                case "list": pb.listContacts(); break;
                case "exit": break;
             }
        }
    }
}
