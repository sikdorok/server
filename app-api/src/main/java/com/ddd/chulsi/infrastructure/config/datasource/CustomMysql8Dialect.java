package com.ddd.chulsi.infrastructure.config.datasource;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * Mysql navive function 등록하기 위한 클래스
 */
public class CustomMysql8Dialect extends MySQLDialect {
    
    public CustomMysql8Dialect() {
        super(DatabaseVersion.make(8));
        
        // native function 추가
//        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
//        registerFunction("json_extract", new StandardSQLFunction("json_extract", StandardBasicTypes.STRING));
//        registerFunction("json_unquote", new StandardSQLFunction("json_unquote", StandardBasicTypes.STRING));
//        registerFunction("timestampdiff", new StandardSQLFunction("timestampdiff", StandardBasicTypes.STRING));
//        registerFunction("ifnull", new StandardSQLFunction("IFNULL(?1, ?2)", StandardBasicTypes.STRING));
    }

}
