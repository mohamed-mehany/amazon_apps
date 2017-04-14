package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;
import services.Cache;

public class LoginCmd extends Command implements Runnable {
    
    public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
    throws Exception {
        
        CallableStatement sqlProc;
        StringBuffer strbufResult = null, strbufResponseJSON;
        String strSessionID, strEmail, strPassword, strFirstName, strClientIP;
        int nSQLResult;
        
        strEmail = ((String) mapUserData.get("email"));
        strPassword = ((String) mapUserData.get("password"));
        
        if (strEmail == null || strEmail.trim().length() == 0 || strPassword == null
            || strPassword.trim().length() == 0)
            return null;
        
        if (!EmailVerifier.verify(strEmail))
            return null;
        
        sqlProc = connection.prepareCall("{call user_login(?,?,?)}");
        sqlProc.setString(1, strEmail);
        sqlProc.setString(2, strPassword);
        sqlProc.registerOutParameter(3, Types.INTEGER);
        sqlProc.execute();
        nSQLResult = sqlProc.getInt(3);
        sqlProc.close();
        if (nSQLResult >= 0) {
            System.out.println("user logged in successfully");
            System.err.println(" user logged in successfully" );
            
        } else{
            System.out.println(" user failed to log in " );
            System.err.println(" user failed to log in " );
        }
        strbufResult = makeJSONResponseEnvelope(0, null, null);
        //strbufResult= String.valueOf(nSQLResult);
        
        return strbufResult;
    }
}
