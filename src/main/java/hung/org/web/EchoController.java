package hung.org.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping
	public Echo echo(HttpSession httpSession) {
		Echo echo = jdbcTemplate.query("SELECT USER() as user, VERSION() as ver, DATABASE() as db",new ResultSetExtractor<Echo>() {

			@Override
			public Echo extractData(ResultSet rs) throws SQLException, DataAccessException {
				Echo echo = new Echo();
				rs.next();
				echo.dbUser = rs.getString("user");
				echo.dbVersion = rs.getString("version");
				echo.database = rs.getString("db");
				
				return echo;
			}
			
		});
		echo.sessionId = httpSession.getId();
		try {
			echo.ip = InetAddress.getLocalHost();
			echo.hostname = echo.ip.getHostName();
		} catch (UnknownHostException e) {
			echo.hostname = "Unknown!!";
		}
		
		return echo;
	}

	static public class Echo {
		
		String sessionId;
		InetAddress ip;
		String hostname;
		String dbUser;
		String dbVersion;
		String database;
	}
}
