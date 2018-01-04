#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
/*
 * This file is generated by jOOQ.
*/
package ${package}.database.or.core;


import javax.annotation.Generated;

import ${package}.database.or.core.tables.Code;
import ${package}.database.or.core.tables.Document;
import ${package}.database.or.core.tables.Person;
import ${package}.database.or.core.tables.Role;
import ${package}.database.or.core.tables.RolePermission;
import ${package}.database.or.core.tables.Text;
import ${package}.database.or.core.tables.Type;
import ${package}.database.or.core.tables.User;
import ${package}.database.or.core.tables.UserRole;


/**
 * Convenience access to all tables in core
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>core.CODE</code>.
     */
    public static final Code CODE = ${package}.database.or.core.tables.Code.CODE;

    /**
     * The table <code>core.DOCUMENT</code>.
     */
    public static final Document DOCUMENT = ${package}.database.or.core.tables.Document.DOCUMENT;

    /**
     * The table <code>core.PERSON</code>.
     */
    public static final Person PERSON = ${package}.database.or.core.tables.Person.PERSON;

    /**
     * The table <code>core.ROLE</code>.
     */
    public static final Role ROLE = ${package}.database.or.core.tables.Role.ROLE;

    /**
     * The table <code>core.ROLE_PERMISSION</code>.
     */
    public static final RolePermission ROLE_PERMISSION = ${package}.database.or.core.tables.RolePermission.ROLE_PERMISSION;

    /**
     * The table <code>core.TEXT</code>.
     */
    public static final Text TEXT = ${package}.database.or.core.tables.Text.TEXT;

    /**
     * The table <code>core.TYPE</code>.
     */
    public static final Type TYPE = ${package}.database.or.core.tables.Type.TYPE;

    /**
     * The table <code>core.USER</code>.
     */
    public static final User USER = ${package}.database.or.core.tables.User.USER;

    /**
     * The table <code>core.USER_ROLE</code>.
     */
    public static final UserRole USER_ROLE = ${package}.database.or.core.tables.UserRole.USER_ROLE;
}
