package carsharing;

import java.util.Scanner;

public class Menu {

    private final CustomerDao customerDao;
    CompanyDao companyDao;
    CarDao carDao;
    Scanner scanner = new Scanner(System.in);
    MenuState state = MenuState.MAIN;

    Company currentCompany = null;
    Customer currentCustomer = null;

    Menu(CompanyDao companyDao, CarDao carDao, CustomerDao customerDao) {
        this.companyDao = companyDao;
        this.carDao = carDao;
        this.customerDao = customerDao;
    }

    void run() {
        while (state != MenuState.EXIT) {
            state = switch (state) {
                case MAIN -> main();
                case EXIT -> throw new RuntimeException("Impossible");
                case LIST -> list();
                case MANAGER -> manager();
                case CREATE -> create();
                case COMPANY -> company();
                case LIST_CARS -> listCars();
                case CREATE_CAR -> createCar();
                case LIST_CUSTOMERS -> listCustomers();
                case CREATE_CUSTOMER -> createCustomer();
                case CUSTOMER -> customer();
                case RENT_CAR -> rentCar();
                case CHOOSE_CAR -> chooseCar();
                case RETURN_CAR -> returnCar();
                case MY_CAR -> myCar();
            };
        }
    }

    private MenuState chooseCar() {
        var cars = carDao.listAvailable(currentCompany);
        if (cars.isEmpty()) {
            System.out.println("No available cars in the " + currentCompany.name());
            return MenuState.CUSTOMER;
        }
        int i = 1;
        for (var car :
                cars) {
            System.out.println(i++ + ". " + car.name());
        }
        System.out.println("0. Back");
        var id = scanner.nextLine();
        if ("0".equals(id)) {
            return MenuState.CUSTOMER;
        }
        try {
            var car = cars.get(Integer.parseInt(id) - 1);
            System.out.println("You rented '" + car.name() + "'");
            currentCustomer = customerDao.setCar(currentCustomer, car);
        } catch (Exception e) {
            System.out.println("Invalid option");
            return state;
        }
        return MenuState.CUSTOMER;
    }

    private MenuState myCar() {
        if (currentCustomer.rentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            return MenuState.CUSTOMER;
        }
        var car = carDao.get(currentCustomer.rentedCarId());
        var company = companyDao.get(car.companyId());
        System.out.println("""
                Your rented car:
                %s
                Company:
                %s
                """.formatted(car.name(), company.name()));
        return MenuState.CUSTOMER;
    }

    private MenuState returnCar() {
        if (currentCustomer.rentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            return MenuState.CUSTOMER;
        }
        System.out.println("You've returned a rented car!");
        currentCustomer = customerDao.setCar(currentCustomer, null);
        return MenuState.CUSTOMER;
    }

    private MenuState rentCar() {
        if (currentCustomer.rentedCarId() != null) {
            System.out.println("You've already rented a car!");
            return MenuState.CUSTOMER;
        }
        var companies = companyDao.list();
        int i = 1;
        for (var company :
                companies) {
            System.out.println(i++ + ". " + company.name());
        }
        System.out.println("0. Back");
        var id = scanner.nextLine();
        if ("0".equals(id)) {
            return MenuState.CUSTOMER;
        }
        try {
            currentCompany = companies.get(Integer.parseInt(id) - 1);
        } catch (Exception e) {
            System.out.println("Invalid option");
            return state;
        }
        return MenuState.CHOOSE_CAR;
    }

    private MenuState customer() {
        System.out.println("""
                1. Rent a car
                2. Return a rented car
                3. My rented car
                0. Back
                """);
        return switch (scanner.nextLine()) {
            case "1" -> MenuState.RENT_CAR;
            case "2" -> MenuState.RETURN_CAR;
            case "3" -> MenuState.MY_CAR;
            case "0" -> MenuState.MAIN;
            default -> {
                System.out.println("Invalid option");
                yield state;
            }
        };
    }

    private MenuState createCustomer() {
        System.out.println("Enter the customer name:");
        var name = scanner.nextLine();
        customerDao.add(name);
        return MenuState.MAIN;
    }

    private MenuState listCustomers() {
        System.out.println("Choose a customer:");
        var customers = customerDao.list();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
            return MenuState.MAIN;
        }
        int i = 1;
        for (var customer :
                customers) {
            System.out.println(i++ + ". " + customer.name());
        }
        System.out.println("0. Back");
        System.out.println();
        currentCustomer = null;
        var id = scanner.nextLine();
        if ("0".equals(id)) return MenuState.MAIN;
        try {
            currentCustomer = customers.get(Integer.parseInt(id) - 1);
        } catch (Exception e) {
            System.out.println("Invalid choice");
            return state;
        }
        return MenuState.CUSTOMER;
    }

    private MenuState createCar() {
        System.out.println("Enter the car name:");
        var name = scanner.nextLine();
        carDao.add(name, currentCompany);
        return MenuState.COMPANY;
    }

    private MenuState listCars() {
        System.out.println("%s cars:".formatted(currentCompany.name()));
        var cars = carDao.list(currentCompany);
        var id = 1;
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
            return MenuState.COMPANY;
        }
        for (Car c : cars) {
            System.out.println(id++ + ". " + c.name());
        }
        System.out.println();
        return MenuState.COMPANY;
    }

    MenuState main() {
        System.out.println("""
                1. Log in as a manager
                2. Log in as a customer
                3. Create a customer
                0. Exit
                """);
        var answer = scanner.nextLine();
        return switch (answer) {
            case "0" -> MenuState.EXIT;
            case "1" -> MenuState.MANAGER;
            case "2" -> MenuState.LIST_CUSTOMERS;
            case "3" -> MenuState.CREATE_CUSTOMER;
            default -> state;
        };
    }

    MenuState manager() {
        System.out.println("""
                1. Company list
                2. Create a company
                0. Back
                """);
        var answer = scanner.nextLine();
        return switch (answer) {
            case "0" -> MenuState.MAIN;
            case "1" -> MenuState.LIST;
            case "2" -> MenuState.CREATE;
            default -> state;
        };
    }

    MenuState list() {
        System.out.println("Choose a company:");
        var companies = companyDao.list();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
            return MenuState.MANAGER;
        }
        int id = 1;
        for (Company c : companies) {
            System.out.println(id++ + ". " + c.name());
        }
        System.out.println("0. Back");
        System.out.println();
        currentCompany = null;
        var answer = scanner.nextLine();
        if ("0".equals(answer)) return MenuState.MANAGER;
        try {
            currentCompany = companies.get(Integer.parseInt(answer) - 1);
        } catch (Exception e) {
            System.out.println("Company not found... Try again");
            return state;
        }
        return MenuState.COMPANY;
    }

    MenuState create() {
        System.out.println("Enter the company name:");
        var name = scanner.nextLine();
        companyDao.add(name);
        return MenuState.MANAGER;
    }

    MenuState company() {
        System.out.println("""
                %s company:
                1. Car list
                2. Create a car
                0. Back
                """.formatted(currentCompany.name()));
        return switch (scanner.nextLine()) {
            case "1" -> MenuState.LIST_CARS;
            case "2" -> MenuState.CREATE_CAR;
            case "0" -> MenuState.MANAGER;
            default -> state;
        };
    }
}
