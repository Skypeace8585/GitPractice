package dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.KadaiAccount;
import util.GenerateHashedPw;
import util.GenerateSalt;

public class KadaiAccountDAO {

	private static Connection getConnection() throws URISyntaxException, SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    URI dbUri = new URI(System.getenv("DATABASE_URL"));

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

	    return DriverManager.getConnection(dbUrl, username, password);
	}
	
	public static int registerAccount(KadaiAccount account) {
		String sql = "INSERT INTO account4 VALUES(default, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
		int result = 0;
		
		// ランダムなソルトの取得(今回は32桁で実装)
		String salt = GenerateSalt.getSalt(32);
		
		// 取得したソルトを使って平文PWをハッシュ
		String hashedPw = GenerateHashedPw.getSafetyPassword(account.getPassword(), salt);
		System.out.println(salt);
		System.out.println(hashedPw);
		try (
				Connection con = getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql);
				){
			pstmt.setString(1, account.getName());
			pstmt.setInt(2,account.getAge());
			pstmt.setString(3, account.getGender());
			pstmt.setString(4, account.getPhone());
			pstmt.setString(5, account.getMail());
			pstmt.setString(6, salt);
			pstmt.setString(7, hashedPw);

			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			System.out.println(result + "件更新しました。");
		}
		return result;
	}
	


// メールアドレスを元にソルトを取得
public static String getSalt(String mail) {
	String sql = "SELECT salt FROM account4 WHERE mail = ?";
	
	try (
			Connection con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			){
		pstmt.setString(1, mail);

		try (ResultSet rs = pstmt.executeQuery()){
			
			if(rs.next()) {
				String salt = rs.getString("salt");
				return salt;
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (URISyntaxException e) {
		e.printStackTrace();
	}
	return null;
}

// ログイン処理
public static KadaiAccount login(String mail, String hashedPw) {
	String sql = "SELECT * FROM account4 WHERE mail = ? AND password = ?";
	
	try (
			Connection con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			){
		pstmt.setString(1, mail);
		pstmt.setString(2, hashedPw);
		try (ResultSet rs = pstmt.executeQuery()){
			
			if(rs.next()) {
				String name = rs.getString("name");
				String age=rs.getString("age");
		        int ageInt = Integer.parseInt(age);
		        String gender =rs.getString("gender");
		        String phone=rs.getString("phone");
		        String salt=rs.getString("salt");
				
				
				KadaiAccount account = new KadaiAccount(-1,name,ageInt,gender,phone,mail,salt,null,null);
				return account;
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (URISyntaxException e) {
		e.printStackTrace();
	}
	return null;
}
}