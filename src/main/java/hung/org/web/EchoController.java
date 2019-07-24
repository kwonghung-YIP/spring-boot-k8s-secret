package hung.org.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hung.org.pojo.Echo;

@RestController
public class EchoController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping
	public Echo echo(HttpSession httpSession, Principal principal) {
		Echo echo = jdbcTemplate.query("SELECT USER() as user, VERSION() as ver, DATABASE() as db",new ResultSetExtractor<Echo>() {

			@Override
			public Echo extractData(ResultSet rs) throws SQLException, DataAccessException {
				Echo echo = new Echo();
				rs.next();
				echo.dbUser = rs.getString("user");
				echo.dbVersion = rs.getString("ver");
				echo.database = rs.getString("db");
				
				return echo;
			}
			
		});
		echo.loginName = principal.getName();
		echo.sessionId = httpSession.getId();
		try {
			echo.hostIp = InetAddress.getLocalHost().getHostAddress();
			echo.hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			echo.hostname = "Unknown!!";
		}
		
		return echo;
	}
}
