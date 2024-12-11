import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

// Book class
class Book {
    private int id;
    private String title;
    private String author;
    private String publisher;
    private boolean isAvailable;

    public Book(int id, String title, String author, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isAvailable = true;
    }

    // Getters and setters 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }

    public void issueBook() {
        if (isAvailable) {
            isAvailable = false;
        }
    }

    public void returnBook() {
        if (!isAvailable) {
            isAvailable = true;
        }
    }
}

// Member class
class Member {
    private int id;
    private String name;
    private String membershipType;
    private ArrayList<Book> borrowedBooks;

    public Member(int id, String name, String membershipType) {
        this.id = id;
        this.name = name;
        this.membershipType = membershipType;
        this.borrowedBooks = new ArrayList<>();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    public void borrowBook(Book book) {
        if (book.isAvailable()) {
            borrowedBooks.add(book);
            book.issueBook();
        }
    }

    public void returnBook(Book book) {
        if (borrowedBooks.contains(book)) {
            borrowedBooks.remove(book);
            book.returnBook();
        }
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void displayBorrowedBooks() {
        System.out.println("Books borrowed by " + name + ":");
        for (Book book : borrowedBooks) {
            System.out.println(book.getTitle() + " by " + book.getAuthor());
        }
    }
}

// Librarian class
class Librarian extends Member {
    private String department;

    public Librarian(int id, String name, String membershipType, String department) {
        super(id, name, membershipType);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public void addBookToLibrary(Library library, Book book) {
        library.addBook(book);
    }

    public void removeBookFromLibrary(Library library, Book book) {
        library.removeBook(book);
    }
}

// Transaction class
class Transaction {
    private static int transactionCounter = 1;
    private int transactionId;
    private Book book;
    private Member member;
    private Date issueDate;
    private Date returnDate;

    public Transaction(Book book, Member member) {
        this.transactionId = transactionCounter++;
        this.book = book;
        this.member = member;
        this.issueDate = new Date();
    }

    // Getters and setters
    public int getTransactionId() { return transactionId; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public Date getIssueDate() { return issueDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
}

// Library class
class Library {
    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private ArrayList<Transaction> transactions;

    private final String BOOK_FILE = "books.txt";
    private final String MEMBER_FILE = "members.txt";
    private final String TRANSACTION_FILE = "transactions.txt";

    public Library() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        transactions = new ArrayList<>();
        loadBooks();
        loadMembers();
        loadTransactions();
    }

    // File I/O methods
    private void loadBooks() {
        try (BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String author = parts[2].trim();
                String publisher = parts[3].trim();
                boolean isAvailable = Boolean.parseBoolean(parts[4].trim());
                books.add(new Book(id, title, author, publisher));
                books.get(books.size() - 1).setAvailable(isAvailable);
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    private void saveBooks() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOK_FILE))) {
            for (Book book : books) {
                pw.println(book.getId() + "," + book.getTitle() + "," + book.getAuthor() + "," +
                           book.getPublisher() + "," + book.isAvailable());
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    private void loadMembers() {
        try (BufferedReader br = new BufferedReader(new FileReader(MEMBER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String membershipType = parts[2].trim();
                members.add(new Member(id, name, membershipType));
            }
        } catch (IOException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    private void saveMembers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MEMBER_FILE))) {
            for (Member member : members) {
                pw.println(member.getId() + "," + member.getName() + "," + member.getMembershipType());
            }
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int transactionId = Integer.parseInt(parts[0].trim());
                int bookId = Integer.parseInt(parts[1].trim());
                int memberId = Integer.parseInt(parts[2].trim());
                Date issueDate = new Date(Long.parseLong(parts[3].trim()));
                Date returnDate = parts.length > 4 ? new Date(Long.parseLong(parts[4].trim())) : null;

                Book book = findBookById(bookId);
                Member member = findMemberById(memberId);
                if (book != null && member != null) {
                    Transaction transaction = new Transaction(book, member);
                    transaction.setReturnDate(returnDate);
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    private void saveTransactions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTION_FILE))) {
            for (Transaction transaction : transactions) {
                pw.println(transaction.getTransactionId() + "," + transaction.getBook().getId() + "," +
                           transaction.getMember().getId() + "," + transaction.getIssueDate().getTime() +
                           (transaction.getReturnDate() != null ? "," + transaction.getReturnDate().getTime() : ""));
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private Book findBookById(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    private Member findMemberById(int id) {
        for (Member member : members) {
            if (member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    // Override add/remove methods to include saving
    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    public void removeBook(Book book) {
        books.remove(book);
        saveBooks();
    }

    public void addMember(Member member) {
        members.add(member);
        saveMembers();
    }

    public void removeMember(Member member) {
        members.remove(member);
        saveMembers();
    }

    public void issueBook(Book book, Member member) {
        if (book.isAvailable()) {
            member.borrowBook(book);
            Transaction transaction = new Transaction(book, member);
            transactions.add(transaction);
            saveBooks();
            saveTransactions();
        } else {
            System.out.println("Book is not available.");
        }
    }

    public void returnBook(Book book, Member member) {
        if (member.getBorrowedBooks().contains(book)) {
            member.returnBook(book);
            for (Transaction transaction : transactions) {
                if (transaction.getBook() == book && transaction.getMember() == member) {
                    transaction.setReturnDate(new Date());
                    break;
                }
            }
            saveBooks();
            saveTransactions();
        }
    }

    public Book searchBook(String title) {
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                return book;
            }
        }
        return null;
    }

    public Member searchMember(String name) {
        for (Member member : members) {
            if (member.getName().toLowerCase().contains(name.toLowerCase())) {
                return member;
            }
        }
        return null;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}

// Library Management GUI
public class LibraryManagementSystem {
    private JFrame frame;
    private Library library;
    private JTextArea outputArea;

    public LibraryManagementSystem() {
        library = new Library();
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));

        // Main panel with light blue background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(173, 216, 230));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setOpaque(false);

        // Buttons
        JButton[] buttons = {
            new JButton("Add Book"),
            new JButton("View Books"),
            new JButton("Add Member"),
            new JButton("Issue Book"),
            new JButton("Return Book"),
            new JButton("View Transactions")
        };

        // Search panels
        JPanel bookSearchPanel = createSearchPanel("Search Book", library::searchBook);
        JPanel memberSearchPanel = createSearchPanel("Search Member", library::searchMember);

        // Output area
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add action listeners to buttons
        buttons[0].addActionListener(e -> addBook());
        buttons[1].addActionListener(e -> viewBooks());
        buttons[2].addActionListener(e -> addMember());
        buttons[3].addActionListener(e -> issueBook());
        buttons[4].addActionListener(e -> returnBook());
        buttons[5].addActionListener(e -> viewTransactions());

        // Style buttons
        for (JButton button : buttons) {
            button.setBackground(new Color(100, 149, 237));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(200, 50));
            buttonPanel.add(button);
        }

        // Arrange components
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bookSearchPanel, BorderLayout.NORTH);
        mainPanel.add(memberSearchPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private <T> JPanel createSearchPanel(String label, java.util.function.Function<String, T> searchMethod) {
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton(label);
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            T result = searchMethod.apply(searchTerm);
            
            if (result != null) {
                if (result instanceof Book) {
                    Book book = (Book) result;
                    outputArea.setText("Book Found: " + book.getTitle() + 
                                       "\nAuthor: " + book.getAuthor() + 
                                       "\nAvailable: " + book.isAvailable());
                } else if (result instanceof Member) {
                    Member member = (Member) result;
                    outputArea.setText("Member Found: " + member.getName() + 
                                       "\nMembership Type: " + member.getMembershipType());
                }
            } else {
                outputArea.setText("No " + label.replace("Search ", "").toLowerCase() + " found.");
            }
        });

        searchPanel.add(new JLabel(label + ": "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private void addBook() {
        String title = JOptionPane.showInputDialog("Enter Book Title:");
        if (title == null || title.trim().isEmpty()) return;

        String author = JOptionPane.showInputDialog("Enter Author Name:");
        if (author == null || author.trim().isEmpty()) return;

        String publisher = JOptionPane.showInputDialog("Enter Publisher:");
        if (publisher == null || publisher.trim().isEmpty()) return;

        Book newBook = new Book(library.getBooks().size() + 1, title, author, publisher);
        library.addBook(newBook);
        outputArea.append("Book added: " + title + " by " + author + "\n");
    }

    private void viewBooks() {
        outputArea.setText("Library Books:\n");
        for (Book book : library.getBooks()) {
            outputArea.append(book.getTitle() + " by " + book.getAuthor() + 
                              " (Available: " + book.isAvailable() + ")\n");
        }
    }

    private void addMember() {
        String name = JOptionPane.showInputDialog("Enter Member Name:");
        if (name == null || name.trim().isEmpty()) return;

        String membershipType = JOptionPane.showInputDialog("Enter Membership Type:");
        if (membershipType == null || membershipType.trim().isEmpty()) return;

        Member newMember = new Member(library.getMembers().size() + 1, name, membershipType);
        library.addMember(newMember);
        outputArea.append("Member added: " + name + "\n");
    }

    private void issueBook() {
        String bookTitle = JOptionPane.showInputDialog("Enter Book Title:");
        String memberName = JOptionPane.showInputDialog("Enter Member Name:");

        Book book = library.searchBook(bookTitle);
        Member member = library.searchMember(memberName);

        if (book != null && member != null) {
            library.issueBook(book, member);
            outputArea.append("Book '" + book.getTitle() + "' issued to " + member.getName() + "\n");
        } else {
            outputArea.append("Book or Member not found.\n");
        }
    }

    private void returnBook() {
        String bookTitle = JOptionPane.showInputDialog("Enter Book Title:");
        String memberName = JOptionPane.showInputDialog("Enter Member Name:");

        Book book = library.searchBook(bookTitle);
        Member member = library.searchMember(memberName);

        if (book != null && member != null) {
            library.returnBook(book, member);
            outputArea.append("Book '" + book.getTitle() + "' returned by " + member.getName() + "\n");
        } else {
            outputArea.append("Book or Member not found.\n");
        }
    }

    private void viewTransactions() {
        outputArea.setText("Transaction History:\n");
        for (Transaction transaction : library.getTransactions()) {
            outputArea.append("Book: " + transaction.getBook().getTitle() + 
                              ", Member: " + transaction.getMember().getName() + 
                              ", Issue Date: " + transaction.getIssueDate() + "\n");
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryManagementSystem().show();
        });
    }
}
