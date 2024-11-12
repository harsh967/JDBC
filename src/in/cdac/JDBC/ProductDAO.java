package in.cdac.JDBC;
import java.util.*;
//package in.cdac.jdbc;
import java.sql.*;
public class ProductDAO {
 public static void main(String[] args) {
     
 
try{
	Scanner sc =new Scanner(System.in);
	System.out.print("Enter Number of rows you want to Insert : ");
	int n=sc.nextInt();
Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Product","root","Harsh@123");
System.out.println(conn);
Statement stmt=conn.createStatement();
for(int i=0;i<n;i++) {
String name=sc.next();
	double price=sc.nextDouble();
	String Quantity=sc.next();
	
var rs=stmt.executeUpdate("insert into product(product_Name,product_Price,product_Quantity) values('Mobile',20000,'10')");
System.out.println(rs+"Rows Inserted");
}
ResultSet res=stmt.executeQuery("select * from product");
//while(res.next()) {
//	System.out.println("ID : "+res.getString(1) );.
//	System.out.println("Name : "+res.getString(2) );
//	System.out.println("Price : "+res.getDouble(3) );
//	System.out.println("Quantity : "+res.getString(4) );
//
//
//}
}
catch(Exception ex){
ex.printStackTrace();

}

}}
