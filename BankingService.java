import java.io.*;
import java.util.Scanner;
import java.util.UUID;
public class BankingService implements java.io.Serializable {
    private double balance = 0.0;
    private String id;
    private String name;
    private int pin;
    private static final Scanner scanner = new Scanner(System.in);
    private BankingService customerAccount = null;

    public static void main(String[] args){
        BankingService demoAccount = new BankingService();
        demoAccount.loginOrSignUp();
        demoAccount.redirectToChoice();

    }
    private void serialize(BankingService bankingService) {
        try {
            FileOutputStream fos = new FileOutputStream("/home/trimita/Desktop/Serialized/"+ bankingService.id+".SER");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bankingService);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("Account Creation Failed. ");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Account Creation Failed. ");
        }
    }
    private void createAccount() {
        System.out.print("Enter Your Name : ");
        String name = scanner.next();
        System.out.print("Enter Your Pin : ");
        int pin = scanner.nextInt();
        String id = UUID.randomUUID().toString();
        BankingService account = new BankingService();
        account.name = name;
        account.pin = pin;
        account.id = id;
        account.balance = 0.0;
        serialize(account);
        System.out.println("Account Creation Successful. ");
        System.out.println("Your Account No is : "+ account.id);
        showUI();
    }
    private void showUI() {
        System.out.println("\nWelcome to our Bank ");
        System.out.println("Choose an Option : ");
        System.out.println("    Press 1 : Check Balance");
        System.out.println("    Press 2 : Deposit ");
        System.out.println("    Press 3 : Withdraw ");
        System.out.println("    Press 4 : Send Money ");
        System.out.println("    Press 5 : Exit ");
    }

    private void loginOrSignUp() {
        System.out.println("Press 1 : Login ");
        System.out.println("Press 2 : Create Account ");
        System.out.print("Enter Choice : ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            deserialize();
            System.out.print("Enter Your Pin : ");
            int pin = scanner.nextInt();
            boolean validation = validateData(pin);

            if (validation) {
                showUI();
            } else {
                System.out.println("CREDENTIALS DON'T MATCH !!!!");
                System.exit(-1);
            }

        } else if (choice == 2) {
            createAccount();
        } else {
            System.out.println("Invalid Key !! ");
            System.exit(4);
        }
    }

    private void redirectToChoice() {
        System.out.print("Enter Your choice here >> ");
        int choice = scanner.nextInt();
        boolean flag = true;
        while (flag) {
            switch (choice) {
            case 1-> {
                System.out.println("\nChecking Balance");
                checkBalance();
                flag = false;
            }
            case 2 -> {
                System.out.println("\nDeposit");
                deposit();
                flag = false;
            }
            case 3 -> {
                withdraw();
                flag = false;
            }
            case 4 -> {
                sendMoney();
                flag = false;
            }
            case 5 -> {
                flag = false;
            }
            default -> {
                    System.out.println("Invalid choice!!!");
                    showUI();
                    System.out.print("Enter Your choice here >> ");
                    choice = scanner.nextInt();
                }
            }
        }
    }

    private void checkBalance() {
            deserialize();
            System.out.println("Your Balance is : "+ customerAccount.balance);
            makeDecision();
    }

    private void deserialize() {
        System.out.print("Enter Your User ID : ");
        String id = scanner.next();
        try{
            FileInputStream fis = new FileInputStream("/home/trimita/Desktop/Serialized/"+id+".SER");
            ObjectInputStream ois = new ObjectInputStream(fis);
            customerAccount = (BankingService)ois.readObject();
            ois.close();
            fis.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("No Account Found !!! ");
            System.exit(4);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(">>>>>>>>   Account Found   <<<<<<<<< ");
    }

    private void sendMoney() {
        System.out.print("\nEnter Receiver Account Number : ");
        String accountNo = scanner.next();
        BankingService receiverAccount = findReceiverAccount(accountNo);
        System.out.print("Enter Amount : ");
        int amount = scanner.nextInt();
        if(amount > 0 && amount < customerAccount.balance) {
            customerAccount.balance -= amount;
            receiverAccount.balance += amount;
            serialize(customerAccount);
            serialize(receiverAccount);
            System.out.println("TRANSACTION SUCCESSFUL. ");
            System.out.println("Current Balance : "+customerAccount.balance);
            makeDecision();
        }
        else {
            System.out.println("Insufficient Funds!!!");
            makeDecision();
        }
    }

    private BankingService findReceiverAccount(String id) {
        BankingService receiverAccount = null;
        try{
            FileInputStream fis = new FileInputStream("/home/trimita/Desktop/Serialized/"+id+".SER");
            ObjectInputStream ois = new ObjectInputStream(fis);
            receiverAccount = (BankingService)ois.readObject();
            ois.close();
            fis.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("No Account Found !!! ");
            System.exit(4);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(">>>>>>>>   Account Found   <<<<<<<<< ");
        return receiverAccount;
    }


    private boolean validateData(int pin) {
        return customerAccount.pin == pin;
    }

    private void deposit() {
        deserialize();
        System.out.print("Enter amount for deposit : ");
        int  balance = scanner.nextInt();
        customerAccount.balance = customerAccount.balance + balance;
        serialize(customerAccount);
        System.out.println("SUCCESSFULLY DEPOSITED.");
        System.out.println("Current Balance : "+customerAccount.balance);
        makeDecision();
    }

    private void withdraw() {
        System.out.print("Enter Your Pin : ");
        int pin = scanner.nextInt();
        boolean withdrawValidation = validateData(pin);

        if (withdrawValidation) {
            System.out.print("Enter Amount for Withdraw  : ");
            int amount = scanner.nextInt();
            if (amount > 0 && amount < customerAccount.balance) {
                customerAccount.balance = customerAccount.balance - amount;
                System.out.println("SUCCESSFULLY WITHDRAWN.");
                System.out.println("Current Balance : "+customerAccount.balance);
            }
            else {
                System.out.println("Insufficient Fund!!");
            }
        } else
            System.out.println("Wrong Pin!!!");
        serialize(customerAccount);
        makeDecision();
    }


    private void makeDecision() {
        System.out.print("\nContinue ? Y/N : ");
        String decision = scanner.next();
        if(decision.equalsIgnoreCase("Y")) {
            showUI();
            redirectToChoice();
        }
        else {
            System.exit(400);
        }
    }
}

