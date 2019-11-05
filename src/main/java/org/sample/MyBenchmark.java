/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of Oracle nor the names of its contributors may be used
 * to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;

import microsoft.sql.DateTimeOffset;


public class MyBenchmark {

    private static TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
    private static ZoneId zoneID = timeZone.toZoneId();
    private static String connectionUrl = "jdbc:sqlserver://localhost;userName=sa;portName=1433;password=password;database=test;";

    // Run using "mvn clean install" into "java -jar target/benchmarks.jar"
    private static java.sql.Timestamp ts = Timestamp.valueOf("1581-9-24 15:07:09.081");
    private static java.sql.Timestamp ts2 = Timestamp.valueOf("2019-9-24 15:07:09.081");
    private static DateTimeOffset dto = DateTimeOffset.valueOf(ts, 75);
    private static long tsTimeMillis = ts.getTime();

    private static int year = 2000;
    private static int month = 1;
    private static int date = 2;
    private static int hour = 3;
    private static int minute = 4;
    private static int second = 5;
    private static int nanos = 0;

    private static Connection conn;
    private static SQLServerStatement st;
    private static SQLServerPreparedStatement psCast;
    private static SQLServerPreparedStatement psCast2;
    private static SQLServerPreparedStatement psTable;
    private static SQLServerResultSet rs;
    private static SQLServerResultSet rs2;

    static {
        try {
            conn = DriverManager.getConnection(connectionUrl + ";sendTimeAsDatetime=true");
            st = (SQLServerStatement) conn.createStatement();
            st.execute("CREATE TABLE #tmp (dt datetime2)");
            psCast = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIMEOFFSET)");
            psCast2 = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIMEOFFSET)");
            
            psCast.setTimestamp(1, ts);
            rs = (SQLServerResultSet) psCast.executeQuery();
            
            psCast2.setTimestamp(1, ts2);
            rs2 = (SQLServerResultSet) psCast2.executeQuery();
            
            rs.next();
            rs2.next();
            
            psTable = (SQLServerPreparedStatement) conn.prepareStatement("INSERT INTO #tmp values (?)");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Tests scenario with set / execute / get, using timestamp from before Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testExecuteCastBeforeGregorian(Blackhole bh) throws Exception {
        bh.consume(testExecuteCast(conn, psCast, ts));
    }

    /**
     * Tests scenario with set / execute / get, using timestamp from before Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testExecuteTableBeforeGregorian(Blackhole bh) throws Exception {
        bh.consume(testExecuteTable(conn, psTable, ts));
    }
    
    /**
     * Tests scenario with set / execute / get, using timestamp from after Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testExecuteCastAfterGregorian(Blackhole bh) throws Exception {
        bh.consume(testExecuteCast(conn, psCast, ts2));
    }

    /**
     * Tests scenario with set / execute / get, using timestamp from after Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testExecuteTableAfterGregorian(Blackhole bh) throws Exception {
        bh.consume(testExecuteTable(conn, psTable, ts2));
    }

    /**
     * Tests scenario with set.
     * 
     * @param bh
     */
    @Benchmark
    public void testSetTimestamp(Blackhole bh) throws Exception {
        bh.consume(testSetTimestamp());
    }

    /**
     * Tests scenario with get, using timestamp from before Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testGetTimestampBeforeGregorian(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rs));
    }
    
    /**
     * Tests scenario with get, using timestamp from after Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testGetTimestampAfterGregorian(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rs2));
    }


    /**
     * Tests scenario with set.
     * 
     * @param bh
     */
    @Benchmark
    public void testSetDTO(Blackhole bh) throws Exception {
        bh.consume(testSetDTO());
    }

    /**
     * Tests scenario with get, using timestamp from before Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testGetDTOBeforeGregorian(Blackhole bh) throws Exception {
        bh.consume(testGetDTO(rs));
    }
    
    /**
     * Tests scenario with get, using timestamp from after Gregorian Cutoff
     * 
     * @param bh
     */
    @Benchmark
    public void testGetDTOAfterGregorian(Blackhole bh) throws Exception {
        bh.consume(testGetDTO(rs2));
    }

    // /**
    // * Tests scenario with Calendar object being created from a timestamp, plus modifications.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateCalendarFromTimestampInternalPlusModify(Blackhole bh) throws Exception {
    // bh.consume(testCreateCalendarFromTimestampInternalPlusModify());
    // }
    //
    // /**
    // * Tests scenario with ZonedDateTime object being created from a timestamp, plus modifications.
    // *
    // * @param bh
    // */
    //
    // @Benchmark
    // public void testCreateZonedDateTimeFromTimestampInternalPlusModify(Blackhole bh) throws Exception {
    // bh.consume(testCreateZonedDateTimeFromTimestampInternalPlusModify());
    // }
    //
    //
    // /**
    // * Tests actual scenario with casting and selecting date value.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testSetTimestamp(Blackhole bh) {
    // bh.consume(testCreateCalendarFromTimestampInternal());
    // }

    // /**
    // * Tests scenario with Calendar object being created from a timestamp.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateCalendarFromTimestamp(Blackhole bh) {
    // bh.consume(testCreateCalendarFromTimestampInternal());
    // }
    //
    // /**
    // * Tests scenario with ZonedDateTime object being created from a timestamp.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateZonedDateTimeFromTimestamp(Blackhole bh) {
    // bh.consume(testCreateZonedDateTimeFromTimestampInternal());
    // }
    //
    // /**
    // * Tests scenario with LocalDateTime object being created from a timestamp.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateLocalDateTimeFromTimestamp(Blackhole bh) {
    // bh.consume(testCreateLocalDateTimeFromTimestampInternal());
    // }
    //
    // /**
    // * Tests scenario with driver code path that goes through DDC.convertTemporalToObject with Calendar.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateTimestampFromCalendar(Blackhole bh) {
    // bh.consume(testCreateTimestampFromCalendarInternal());
    // }
    //
    // /**
    // * Tests scenario with driver code path that goes through DDC.convertTemporalToObject with ZonedDateTime.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateTimestampFromZonedDateTime(Blackhole bh) {
    // bh.consume(testCreateTimestampFromZonedDateTimeInternal());
    // }
    //
    // /**
    // * Tests scenario with driver code path that goes through DDC.convertTemporalToObject with LocalDateTime.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateTimestampFromLocalDateTime(Blackhole bh) {
    // bh.consume(testCreateTimestampFromLocalDateTimeInternal());
    // }
    //
    // /**
    // * Tests scenario with where a Calendar object is constructed from parameters.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateCalendarFromParameter(Blackhole bh) {
    // bh.consume(testCreateCalendarFromParameterInternal());
    // }
    //
    // /**
    // * Tests scenario with where a ZonedDateTime object is constructed from parameters.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateZonedDateTimeFromParameter(Blackhole bh) {
    // bh.consume(testCreateZonedDateTimeFromParameterInternal());
    // }
    //
    // /**
    // * Tests scenario with where a LocalDateTime object is constructed from parameters.
    // *
    // * @param bh
    // */
    // @Benchmark
    // public void testCreateLocalDateTimeFromParameter(Blackhole bh) {
    // bh.consume(testCreateLocalDateTimeFromParameterInternal());
    // }

    private Timestamp testExecuteCast(Connection conn, SQLServerPreparedStatement ps, Timestamp ts) throws Exception {
        ps.setDateTime(1, ts);
        Timestamp str = null;
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            str = rs.getTimestamp(1);
        }
        return str;
    }

    private Timestamp testExecuteTable(Connection conn, SQLServerPreparedStatement ps, Timestamp ts) throws Exception {
        ps.setDateTime(1, ts);
        Timestamp str = null;
        ps.executeUpdate();
        try (ResultSet rs = st.executeQuery("SELECT TOP 1000 * from #tmp")) {
            rs.next();
            str = rs.getTimestamp(1);
        }
        return str;
    }

    private int testSetTimestamp() throws Exception {
        psCast.setTimestamp(1, ts);
        return 0;
    }

    private Timestamp testGetTimestamp(SQLServerResultSet rs) throws Exception {
        return rs.getTimestamp(1);
    }

    private int testSetDTO() throws Exception {
        psCast.setDateTimeOffset(1, dto);
        return 0;
    }

    private DateTimeOffset testGetDTO(SQLServerResultSet rs) throws Exception {
        return rs.getDateTimeOffset(1);
    }

    private Calendar testCreateCalendarFromTimestampInternal() {
        GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.clear();
        cal.setTimeInMillis(tsTimeMillis);
        cal.setTimeZone(timeZone);
        return cal;
    }

    private Calendar testCreateCalendarFromTimestampInternalPlusModify() {
        GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.clear();
        cal.setTimeInMillis(tsTimeMillis);
        cal.setTimeZone(timeZone);
        cal.add(Calendar.SECOND, 15);
        cal.add(Calendar.MINUTE, 15);
        cal.add(Calendar.DATE, 15);
        return cal;
    }

    private ZonedDateTime testCreateZonedDateTimeFromTimestampInternal() {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tsTimeMillis), zoneID);
        zdt = zdt.plusSeconds(15);
        zdt = zdt.plusMinutes(15);
        zdt = zdt.plusDays(15);
        return zdt;
    }

    private ZonedDateTime testCreateZonedDateTimeFromTimestampInternalPlusModify() {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tsTimeMillis), zoneID);
        return zdt;
    }

    private LocalDateTime testCreateLocalDateTimeFromTimestampInternal() {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(tsTimeMillis), zoneID);
        return ldt;
    }

    private Timestamp testCreateTimestampFromCalendarInternal() {
        GregorianCalendar cal = new GregorianCalendar(timeZone, Locale.US);
        cal.setLenient(true);
        cal.clear();
        cal.set(1970, 0, 1, 12, 0, 0); // 1970-01-01
        return new Timestamp(cal.getTimeInMillis());
    }

    private Timestamp testCreateTimestampFromZonedDateTimeInternal() {
        ZonedDateTime zepoch = ZonedDateTime.of(1970, 1, 1, 12, 0, 0, 0, zoneID);
        return Timestamp.from(zepoch.toInstant());
    }

    private Timestamp testCreateTimestampFromLocalDateTimeInternal() {
        LocalDateTime lepoch = LocalDateTime.of(1970, 1, 1, 12, 0, 0);
        return Timestamp.valueOf(lepoch);
    }

    private Calendar testCreateCalendarFromParameterInternal() {
        GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar month starts at 0
        cal.set(Calendar.DATE, date);
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.setTimeZone(timeZone);
        return cal;
    }

    private ZonedDateTime testCreateZonedDateTimeFromParameterInternal() {
        ZonedDateTime zdt = ZonedDateTime.of(year, month, date, hour, minute, second, nanos, zoneID);
        return zdt;
    }

    private LocalDateTime testCreateLocalDateTimeFromParameterInternal() {
        LocalDateTime ldt = LocalDateTime.of(year, month, date, hour, minute, second);
        return ldt;
    }
}
