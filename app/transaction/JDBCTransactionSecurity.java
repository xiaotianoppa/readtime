
package transaction;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;

public abstract class JDBCTransactionSecurity<T> {

    private static final Logger log = LoggerFactory.getLogger(JDBCTransactionSecurity.class);

    public static final String TAG = "TransactionSecurity";

    public abstract T operateOnDB();

    protected boolean _newTxStarted = false;

    public T execute() {
        return execute(false, false);
    }

    public T execute(boolean readOnly, boolean fallback) {
        T t = null;
        Connection conn = null;
        try {
            conn = DBBuilder.getThreadConn().get();
            if (conn == null) {
                conn = DBBuilder.getConn();
                conn.setAutoCommit(false);
                DBBuilder.getThreadConn().set(conn);
                _newTxStarted = true;
            }

            t = operateOnDB();
            return t;

        } catch (Exception e) {//包括SQLException，还有RuntimeException
            log.warn(e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    log.warn(e.getMessage(), e);

                }

            }
            return null;
        } finally {
            if (_newTxStarted && conn != null) {
                DBBuilder.getThreadConn().remove();
                try {
                    //conn.setAutoCommit(true);//为true的话，就不能commit了的
                    conn.commit();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
                JDBCBuilder.closeQuitely(conn);
            }
        }
    }

}
