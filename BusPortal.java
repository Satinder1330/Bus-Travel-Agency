package BusTravelAgency;

import java.sql.*;
import java.util.Scanner;

public class BusPortal {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/bus_agency";
    private static final String username="root";
    private static final String password ="Basoli@1313";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println("\t\t WELCOME TO CANADA BUS PORTAL \t");
                System.out.println("PRESS 1 FOR ALL THE BUS SERVICES");
                System.out.println("PRESS 2 TO BOOK BUS SERVICES");
                System.out.println("PRESS 3  TO VIEW YOUR BOOKINGS ");
                System.out.println("PRESS 4 TO EXIT" );
                System.out.println("SELECT YOUR OPTION : ");
                int option = input.nextInt();
                switch (option){
                    case 1:Services(connection);
                    case 2:book(connection,input);
                    case 3:view(connection,input);
                    case 4:System.exit(0);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private  static void Services(Connection connection){
        String query = "SELECT bus_id,bus_name,starting_point,end_point FROM Buses";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int id = resultSet.getInt("bus_id");
                String busName = resultSet.getString("bus_name");
                String start = resultSet.getString("starting_point");
                String end = resultSet.getString("end_point");
                System.out.printf("BUS ID(%d) -%S - %S TO %S",id,busName,start,end);
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
    private static void book(Connection connection,Scanner input){
        System.out.println("ENTER THE BUS ID DO YOU WANT TO BOOK");
        int id = input.nextInt();
        input.nextLine();
        System.out.println("ENTER YOUR NAME ");
        String name = input.next();
        input.nextLine();
        System.out.println("ENTER HOW MANY SEATS DO YOU WANT TO BOOK");
        int seat = input.nextInt();
        String query = "Select total_seats FROM Buses WHERE bus_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalSeats=0;
                if(resultSet.next())
                { totalSeats = resultSet.getInt("total_seats");};
                if(totalSeats>=seat){
                    String insertQuery= "INSERT INTO Bookings (bus_id,passenger_name,seats) values (?,?,?)";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(insertQuery);
                    preparedStatement1.setInt(1,id);
                    preparedStatement1.setString(2,name);
                    preparedStatement1.setInt(3,seat);
                    preparedStatement1.executeUpdate();

                    String seatUpdateQuery = "Update Buses Set total_seats = (total_seats-?) WHERE bus_id = ?";
                    PreparedStatement preparedStatement2 = connection.prepareStatement(seatUpdateQuery);
                    preparedStatement2.setInt(1,seat);
                    preparedStatement2.setInt(2,id);
                    preparedStatement2.execute();
                    String bookingQuery ="SELECT booking_id FROM Bookings";
                    PreparedStatement preparedStatement3 = connection.prepareStatement(bookingQuery);
                    int bookingId = 0;
                    ResultSet resultSet1 = preparedStatement3.executeQuery();

                    if(resultSet1.next()){bookingId = resultSet1.getInt("booking_id");}
                    System.out.println("Your seats are booked and your booking id is "+ bookingId);
                }else {
                    System.out.println("Sorry not enough seats are available ");
                }
                System.exit(0);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    private  static void view(Connection connection , Scanner input){
        System.out.println("ENTER YOUR BOOKING ID");
        int id = input.nextInt();
        String query = "SELECT b.booking_id,b.passenger_name,b.seats,bus.bus_name" +
                ",b.passenger_name FROM Bookings AS b JOIN Buses AS bus ON B.bus_id = bus.bus_id ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                System.out.println("Booking ID: " + rs.getInt("booking_id") +
                        " | Bus: " + rs.getString("bus_name") +
                        " | Passenger: " + rs.getString("passenger_name") +
                        " | Seats: " + rs.getInt("seats"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

}
