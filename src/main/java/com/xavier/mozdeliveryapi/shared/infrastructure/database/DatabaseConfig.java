package com.xavier.mozdeliveryapi.shared.infrastructure.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.xavier.mozdeliveryapi.shared.infrastructure.multitenant.TenantContext;

/**
 * Database configuration for multi-tenant setup with Row Level Security.
 * Only activated when a DataSource is available.
 */
@Configuration
@ConditionalOnBean(DataSource.class)
public class DatabaseConfig {
    
    /**
     * Custom JdbcTemplate that sets tenant context for each connection.
     */
    @Bean
    public JdbcTemplate tenantAwareJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource) {
            @Override
            protected Connection createConnectionProxy(Connection con) {
                return new TenantAwareConnectionProxy(con);
            }
        };
    }
    
    /**
     * Connection proxy that sets the tenant context for Row Level Security.
     */
    private static class TenantAwareConnectionProxy implements Connection {
        private final Connection target;
        
        public TenantAwareConnectionProxy(Connection target) {
            this.target = target;
            setTenantContext();
        }
        
        private void setTenantContext() {
            String tenantId = TenantContext.getCurrentTenant();
            if (tenantId != null) {
                try {
                    target.createStatement().execute(
                        "SET LOCAL app.current_tenant_id = '" + tenantId + "'"
                    );
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set tenant context in database", e);
                }
            }
        }
        
        // Delegate all methods to the target connection
        @Override
        public java.sql.Statement createStatement() throws SQLException {
            return target.createStatement();
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            return target.prepareStatement(sql);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return target.prepareCall(sql);
        }
        
        @Override
        public String nativeSQL(String sql) throws SQLException {
            return target.nativeSQL(sql);
        }
        
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            target.setAutoCommit(autoCommit);
        }
        
        @Override
        public boolean getAutoCommit() throws SQLException {
            return target.getAutoCommit();
        }
        
        @Override
        public void commit() throws SQLException {
            target.commit();
        }
        
        @Override
        public void rollback() throws SQLException {
            target.rollback();
        }
        
        @Override
        public void close() throws SQLException {
            target.close();
        }
        
        @Override
        public boolean isClosed() throws SQLException {
            return target.isClosed();
        }
        
        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return target.getMetaData();
        }
        
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            target.setReadOnly(readOnly);
        }
        
        @Override
        public boolean isReadOnly() throws SQLException {
            return target.isReadOnly();
        }
        
        @Override
        public void setCatalog(String catalog) throws SQLException {
            target.setCatalog(catalog);
        }
        
        @Override
        public String getCatalog() throws SQLException {
            return target.getCatalog();
        }
        
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            target.setTransactionIsolation(level);
        }
        
        @Override
        public int getTransactionIsolation() throws SQLException {
            return target.getTransactionIsolation();
        }
        
        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return target.getWarnings();
        }
        
        @Override
        public void clearWarnings() throws SQLException {
            target.clearWarnings();
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return target.createStatement(resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return target.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return target.prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            return target.getTypeMap();
        }
        
        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            target.setTypeMap(map);
        }
        
        @Override
        public void setHoldability(int holdability) throws SQLException {
            target.setHoldability(holdability);
        }
        
        @Override
        public int getHoldability() throws SQLException {
            return target.getHoldability();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return target.setSavepoint();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return target.setSavepoint(name);
        }
        
        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            target.rollback(savepoint);
        }
        
        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            target.releaseSavepoint(savepoint);
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return target.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return target.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return target.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return target.prepareStatement(sql, autoGeneratedKeys);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return target.prepareStatement(sql, columnIndexes);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return target.prepareStatement(sql, columnNames);
        }
        
        @Override
        public java.sql.Clob createClob() throws SQLException {
            return target.createClob();
        }
        
        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return target.createBlob();
        }
        
        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return target.createNClob();
        }
        
        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return target.createSQLXML();
        }
        
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return target.isValid(timeout);
        }
        
        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            target.setClientInfo(name, value);
        }
        
        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            target.setClientInfo(properties);
        }
        
        @Override
        public String getClientInfo(String name) throws SQLException {
            return target.getClientInfo(name);
        }
        
        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return target.getClientInfo();
        }
        
        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return target.createArrayOf(typeName, elements);
        }
        
        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return target.createStruct(typeName, attributes);
        }
        
        @Override
        public void setSchema(String schema) throws SQLException {
            target.setSchema(schema);
        }
        
        @Override
        public String getSchema() throws SQLException {
            return target.getSchema();
        }
        
        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            target.abort(executor);
        }
        
        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            target.setNetworkTimeout(executor, milliseconds);
        }
        
        @Override
        public int getNetworkTimeout() throws SQLException {
            return target.getNetworkTimeout();
        }
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return target.unwrap(iface);
        }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return target.isWrapperFor(iface);
        }
    }
}