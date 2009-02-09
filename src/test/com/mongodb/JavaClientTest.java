// JavaClientTest.java

package com.mongodb;

import java.io.*;
import java.util.*;

import org.testng.annotations.Test;

import com.mongodb.util.*;

public class JavaClientTest extends TestCase {
    
    public JavaClientTest()
        throws IOException {
        _db = new Mongo( "127.0.0.1" , "jtest" );        
    }

    @Test
    public void test1(){
        DBCollection c = _db.getCollection( "test1" );;
        c.drop();

        DBObject m = new BasicDBObject();
        m.put( "name" , "eliot" );
        m.put( "state" , "ny" );
        
        c.save( m );
        
        assert( m.containsKey( "_id" ) );

        Map out = (Map)(c.find( m.get( "_id" ).toString() ));
        assertEquals( "eliot" , out.get( "name" ) );
        assertEquals( "ny" , out.get( "state" ) );
    }

    @Test
    public void test2(){
        DBCollection c = _db.getCollection( "test2" );;
        c.drop();
        
        DBObject m = new BasicDBObject();
        m.put( "name" , "eliot" );
        m.put( "state" , "ny" );
        
        Map<String,Object> sub = new HashMap<String,Object>();
        sub.put( "bar" , "1z" );
        m.put( "foo" , sub );

        c.save( m );
        
        assert( m.containsKey( "_id" ) );

        Map out = (Map)(c.find( m.get( "_id" ).toString() ));
        assertEquals( "eliot" , out.get( "name" ) );
        assertEquals( "ny" , out.get( "state" ) );

        Map z = (Map)out.get( "foo" );
        assertNotNull( z );
        assertEquals( "1z" , z.get( "bar" ) );
    }

    @Test
    public void testWhere1(){
        DBCollection c = _db.getCollection( "testWhere1" );
        c.drop();
        assertNull( c.findOne() );
        
        c.save( BasicDBObjectBuilder.start().add( "a" , 1 ).get() );
        assertNotNull( c.findOne() != null );
     
        assertNotNull( c.findOne( BasicDBObjectBuilder.start().add( "$where" , "this.a == 1" ).get() ) );
        assertNull( c.findOne( BasicDBObjectBuilder.start().add( "$where" , "this.a == 2" ).get() ) );
    }

    @Test
    public void testCount(){
        DBCollection c = _db.getCollection("testCount");

        c.drop();
        assertNull(c.findOne());
        assertTrue(c.getCount() == 0);

        for (int i=0; i < 100; i++) {
            c.insert(new BasicDBObject().append("i", i));
        }

        assertTrue(c.getCount() == 100);
    }

    @Test
    public void testIndex(){
        DBCollection c = _db.getCollection("testIndex");

        c.drop();
        assertNull(c.findOne());

        for (int i=0; i < 100; i++) {
            c.insert(new BasicDBObject().append("i", i));
        }

        assertTrue(c.getCount() == 100);

        c.createIndex(new BasicDBObject("i", 1));

        List<DBObject> list = c.getIndexInfo();

        assertTrue(list.size() == 1);
        assertTrue(list.get(0).get("name").equals("i_1"));
    }

    @Test
    public void testBinary(){
        DBCollection c = _db.getCollection( "testBinary" );
        c.save( BasicDBObjectBuilder.start().add( "a" , "eliot".getBytes() ).get() );
        
        DBObject out = c.findOne();
        byte[] b = (byte[])(out.get( "a" ) );
        assertEquals( "eliot" , new String( b ) );
    }
    
    final Mongo _db;

    public static void main( String args[] )
        throws Exception {
        (new JavaClientTest()).runConsole();
    }
}
