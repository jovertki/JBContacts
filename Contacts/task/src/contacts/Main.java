package contacts;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

abstract class Contact implements Serializable {

//    public boolean isPerson;

    public String number;
    private final LocalDateTime created;
    private LocalDateTime lastModified;

    final static String NO_DATA = "[no data]";
    final static String NO_NUMBER = "[no number]";

    Contact(String number) {
        this.number = number;
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (!checkPhoneNumber(number)) {
            this.number = NO_NUMBER;
            System.out.println("Wrong number format!");
        } else {
            this.number = number;
        }
        setLastModified(LocalDateTime.now());
    }

    public boolean hasNumber(){
        return !number.equals(NO_NUMBER);
    }

    private static boolean checkPhoneNumber(String phoneNumber) {
        String firstGroup = "[0-9a-zA-Z]+";
        String otherGroup = "([0-9a-zA-Z]{2,})";

        String regexp1 = "\\+?" + firstGroup + "([ |-]" + otherGroup + ")*";
        String regexp2 = "\\+?" +"\\(" + firstGroup + "\\)" + "([ |-]" + otherGroup + ")*";
        String regexp3 = "\\+?" + firstGroup + "[ |-]" + "\\(" + otherGroup + "\\)" + "([ |-]" + otherGroup + ")*";

        return phoneNumber.matches(regexp1) ||phoneNumber.matches(regexp2) || phoneNumber.matches(regexp3);
    }

    abstract public String toString();
    abstract public String toStringShort();
    abstract public String toSearchText();

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    abstract static class Builder {
        private String phoneNumber = NO_NUMBER;

        public Builder setPhoneNumber(String phoneNumber) {
            if (!checkPhoneNumber(phoneNumber)) {
                this.phoneNumber = NO_NUMBER;
                System.out.println("Wrong number format!");
            } else {
                this.phoneNumber = phoneNumber;
            }
            return this;
        }

        abstract public Contact build();

        protected String getPhoneNumber() {
            return phoneNumber;
        }
    }
}

class Organization extends Contact {

    private String name;
    private String address;

    Organization(String name, String address, String phoneNumber) {
        super(phoneNumber);
        this.name = name;
        this.address = address;
    }

    static class Builder extends Contact.Builder{
        private String name = "";
        private String address = NO_DATA;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Contact build(){
            return new Organization(name, address, getPhoneNumber());
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Organization name: " + getName() + "\n" +
                "Address: " + getAddress() + "\n" +
                "Number: " + getNumber() + "\n" +
                "Time created: " + getCreated() + "\n" +
                "Time last edit: " + getLastModified();
    }

    @Override
    public String toStringShort() {
        return getName();
    }

    @Override
    public String toSearchText() {
        return (getName() +" " + getAddress() + " " + getNumber()).toLowerCase();
    }
}

class Person extends Contact {

    private String name;
    private String surname;
    private String birth;
    private String gender;

    Person(String name, String surname, String phoneNumber, String birth, String gender) {
        super(phoneNumber);
        this.name = name;
        this.surname = surname;
        this.birth = birth;
        this.gender = gender;
    }

    static class Builder extends Contact.Builder{
        private String name = "";
        private String surname = NO_DATA;
        private String birth = NO_DATA;
        private String gender = NO_DATA;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Builder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setBirth(String birth) {
            if (checkBirthDate(birth)) {
                this.birth = birth;
            } else {
                this.birth = NO_DATA;
                System.out.println("Bad birth date!");
            }
            return this;
        }

        public Builder setGender(String gender) {
            if(checkGender(gender)) {
                this.gender = gender;
            } else {
                this.gender = NO_DATA;
                System.out.println("Bad gender!");
            }
            return this;
        }

        public Contact build(){
            return new Person(name, surname, getPhoneNumber(), birth, gender);
        }

    }

    static boolean checkGender(String gender){
        return gender.matches("[FM]");
    }

    static boolean checkBirthDate(String birthDate) {

        try {
            LocalDate.parse(birthDate);
        } catch (DateTimeParseException ignore){
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        setLastModified(LocalDateTime.now());
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        setLastModified(LocalDateTime.now());
        this.surname = surname;
    }
    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        if (checkBirthDate(birth)) {
            this.birth = birth;
            setLastModified(LocalDateTime.now());
        } else {
            this.birth = NO_DATA;
            System.out.println("Bad birth date!");
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if(checkGender(gender)) {
            this.gender = gender;
            setLastModified(LocalDateTime.now());
        } else {
            this.gender = NO_DATA;
            System.out.println("Bad gender!");
        }
    }

    @Override
    public String toString() {

        return "Name: " + getName() + "\n" +
                "Surname: " + getSurname() + "\n" +
                "Birth date: " + getBirth() + "\n" +
                "Gender: " + getGender() + "\n" +
                "Number: " + getNumber() + "\n" +
                "Time created: " + getCreated() + "\n" +
                "Time last edit: " + getLastModified();
    }

    @Override
    public String toStringShort() {
        return getName() + " " + getSurname();
    }

    @Override
    public String toSearchText() {
        return (getName() + " " + getSurname() + " " + getNumber() + " "
                + getBirth() + " " + getGender()).toLowerCase();
    }
}

class ContactFactory {

    public Contact makePerson(Scanner input) {
        System.out.print("Enter the name: ");
        Person.Builder out = new Person.Builder()
                .setName(input.nextLine());
        System.out.print("Enter the surname: ");
        out.setSurname(input.nextLine());
        System.out.print("Enter the birth date: ");
        out.setBirth(input.nextLine());
        System.out.print("Enter the gender (M, F): ");
        out.setGender(input.nextLine());
        System.out.print("Enter the number: ");
        out.setPhoneNumber(input.nextLine());

        return out.build();
    }

    public Contact makeOrganization(Scanner input) {
        System.out.print("Enter the organization name: ");
        Organization.Builder out = new Organization.Builder()
                .setName(input.nextLine());
        System.out.print("Enter the address: ");
        out.setAddress(input.nextLine());
        System.out.print("Enter the number: ");
        out.setPhoneNumber(input.nextLine());

        return out.build();
    }

}

class PhoneBook implements Serializable {
    ArrayList<Contact> contacts;
    PhoneBook(){
        contacts = new ArrayList<>();
    }

    public void addContact(Scanner input) {
        ContactFactory cf = new ContactFactory();

        System.out.print("Enter the type (person, organization): ");
        Contact contact;
        switch (input.nextLine().toLowerCase()) {
            case "organization":
                contact = cf.makeOrganization(input); break;
            case "person":
                contact = cf.makePerson(input); break;
            default:
                System.out.println("no such type!"); return;
        }

        contacts.add(contact);
        System.out.println("The record added.");
    }

    public void listContacts(Scanner input) {
        int i = 1;
        for (Contact c : contacts) {
            System.out.println((i) + ". " + c.toStringShort());
            i++;
        }
        listLoop(input);
    }

    public void remove(Contact record) {
        contacts.remove(record);
        System.out.println("The record was removed!");
    }

    private void editContact(Contact contact, Scanner input) {
        ArrayList<Field> fields = new ArrayList<>(List.of(contact.getClass().getDeclaredFields()));
        fields.addAll(List.of(contact.getClass().getFields()));
        System.out.print("Select a field (");
        System.out.print(fields.get(0).getName());
        for (int i = 1; i < fields.size(); i++) {
            System.out.print(", " + fields.get(i).getName());
        }
        System.out.print("): ");
        String inputField = input.nextLine();
        if (fields.stream().anyMatch(f -> f.getName().equals(inputField))) {

            System.out.print("Enter " + inputField + ": ");
            try {
                contact.getClass()
                        .getMethod("set" + inputField.substring(0, 1).toUpperCase() + inputField.substring(1),
                                String.class)
                        .invoke(contact, input.nextLine());
                System.out.println("Saved");
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {
                System.out.println("error processing request");
            }
        } else {
            System.out.println("No such field");
        }

    }

    public void count() {
        System.out.println("The Phone Book has "+ contacts.size() + " records.");
    }


    private ArrayList<Contact> searchConcrete(Scanner input) {
        System.out.print("Enter search query: ");
        String query = input.nextLine().toLowerCase();
        ArrayList<Contact> founds = new ArrayList<>();
        for (Contact c : contacts) {
            String text = c.toSearchText();
            if (text.contains(query) || text.matches(query)) {
                founds.add(c);
            }
        }
        return founds;
    }


    private void recordLoop(Scanner input, Contact record) {
        while (true) {
            System.out.print("\n[record] Enter action (edit, delete, menu): ");
            String action = input.nextLine();

            switch (action) {
                case "edit": editContact(record, input);
                    System.out.println(record); break;
                case "delete": remove(record); return;
                case "menu": return;
                default: System.out.println("no such action.");
            }
        }
    }

    private void listLoop(Scanner input) {
        System.out.print("\n[list] Enter action ([number], back): ");
        String action = input.nextLine();
        if (action.equals("back")) {}
        else if (action.matches("[0-9]+")) {
            int i = Integer.parseInt(action);
            if (i <= contacts.size() && i > 0) {
                Contact record = contacts.get(i - 1);
                System.out.println(record.toString());
                recordLoop(input, record);
            } else {
                System.out.println("No such record.");
            }
        }
    }

    private boolean searchMenuLoop(Scanner input, List<Contact> founds) {
        while (true) {
            System.out.print("\n[search] Enter action ([number], again, back): ");
            String action = input.nextLine();
            if (action.equals("back")) {
                return false;
            } else if (action.equals("again")) {
                break;
            } else if (action.matches("[0-9]+")) {
                int i = Integer.parseInt(action);
                if (i <= founds.size() && i > 0) {
                    Contact record = founds.get(i - 1);
                    System.out.println(record.toString());
                    recordLoop(input, record);
                    return false;
                } else {
                    System.out.println("No such record.");
                }
            }
        }
        return true;
    }

    public void search(Scanner input) {
        while (true) {
            List<Contact> founds = searchConcrete(input);
            if (founds.size() == 0) {
                System.out.println("No records found.");
                return;
            } else {
                System.out.println("Found " + founds.size() + " results: ");
                for (int i = 0; i < founds.size(); i++) {
                    System.out.println((i + 1) + ". " + founds.get(i).toStringShort());
                }
            }
            if (!searchMenuLoop(input, founds)) {
                return;
            }
        }
    }
}


class SerializationUtils {
    /**
     * Serialize the given object to the file
     */
    public static void serialize(PhoneBook obj, String fileName) throws IOException {
        File out = new File(fileName);
        FileOutputStream fos = new FileOutputStream(out);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * Deserialize to an object from the file
     */
    public static PhoneBook deserialize(String fileName) throws IOException, ClassNotFoundException {
        File in = new File(fileName);
        if (!in.exists())
            return new PhoneBook();
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        PhoneBook obj = (PhoneBook) ois.readObject();
        ois.close();
        return obj;
    }
}

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        PhoneBook pb;
        if (args.length == 1) {
            try {
                pb = SerializationUtils.deserialize(args[0]);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading file");
                return;
            }
        } else if (args.length == 0) {
            pb = new PhoneBook();
        } else {
            return;
        }

        String action = "";
        while (!action.equals("exit")) {
            System.out.print("\n[menu] Enter action (add, list, search, count, exit): ");
            action = scanner.nextLine();
            switch (action) {
                case "add": pb.addContact(scanner); break;
                case "search": pb.search(scanner); break;
                case "count": pb.count(); break;
                case "list": pb.listContacts(scanner); break;
                case "exit": break;
             }
        }

        if (args.length == 1) {
            try {
                SerializationUtils.serialize(pb, args[0]);
            } catch (IOException e) {
                System.out.println("Error saving file");
            }
        }
    }
}
