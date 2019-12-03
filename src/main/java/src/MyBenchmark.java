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

package src;

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
    private static java.sql.Timestamp ts3 = Timestamp.valueOf("2004-04-06 02:00:00.0");
    private static java.sql.Timestamp ts4 = Timestamp.valueOf("2004-11-15 02:00:00.0");
    private static java.sql.Timestamp tsCurrent = new Timestamp(System.currentTimeMillis());
    private static DateTimeOffset dto = DateTimeOffset.valueOf(ts, 75);
    private static String timezone = "Pacific/Honolulu";
    private static Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
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
    
    private static SQLServerPreparedStatement psCastDTO;
    private static SQLServerPreparedStatement psCastDate;
    private static SQLServerPreparedStatement psCastSmallDateTime;
    private static SQLServerPreparedStatement psCastDateTime;
    private static SQLServerPreparedStatement psCastDateTime2;
    private static SQLServerPreparedStatement psCastTime;
    
    private static SQLServerPreparedStatement psCastDateTime2Pregregorian;
    private static SQLServerPreparedStatement psCastDateTime2DST;
    private static SQLServerPreparedStatement psCastDateTime2NotDST;
    private static SQLServerPreparedStatement psTable;
    
    private static SQLServerResultSet rsDTO;
    private static SQLServerResultSet rsDate;
    private static SQLServerResultSet rsSmallDateTime;
    private static SQLServerResultSet rsDateTime;
    private static SQLServerResultSet rsDateTime2;
    private static SQLServerResultSet rsTime;
    
    private static SQLServerResultSet rsDateTime2Pregregorian;
    private static SQLServerResultSet rsDateTime2DST;
    private static SQLServerResultSet rsDateTime2NotDST;

    static {
        try {
            conn = DriverManager.getConnection(connectionUrl + ";sendTimeAsDatetime=true");
            st = (SQLServerStatement) conn.createStatement();
            st.execute("CREATE TABLE #tmp (dt datetime2)");
            
            psCastDTO = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIMEOFFSET)");
            psCastDate = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATE)");
            psCastSmallDateTime = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS SMALLDATETIME)");
            psCastDateTime = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIME)");
            psCastDateTime2 = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIME2)");
            psCastTime = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS TIME)");
            
            
            psCastDateTime2Pregregorian = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIME2)");
            psCastDateTime2DST = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIME2)");
            psCastDateTime2NotDST = (SQLServerPreparedStatement) conn.prepareStatement("SELECT CAST(? AS DATETIME2)");
            
            psCastDTO.setTimestamp(1, tsCurrent);
            rsDTO = (SQLServerResultSet) psCastDTO.executeQuery();
            
            psCastDate.setTimestamp(1, tsCurrent);
            rsDate = (SQLServerResultSet) psCastDate.executeQuery();
            
            psCastSmallDateTime.setTimestamp(1, tsCurrent);
            rsSmallDateTime = (SQLServerResultSet) psCastSmallDateTime.executeQuery();
            
            psCastDateTime.setTimestamp(1, tsCurrent);
            rsDateTime = (SQLServerResultSet) psCastDateTime.executeQuery();
            
            psCastDateTime2.setTimestamp(1, tsCurrent);
            rsDateTime2 = (SQLServerResultSet) psCastDateTime2.executeQuery();
            
            psCastTime.setTimestamp(1, tsCurrent);
            rsTime = (SQLServerResultSet) psCastTime.executeQuery();
            
            psCastDateTime2Pregregorian.setTimestamp(1, ts);
            rsDateTime2Pregregorian = (SQLServerResultSet) psCastDateTime2Pregregorian.executeQuery();
            
            psCastDateTime2DST.setTimestamp(1, ts3);
            rsDateTime2DST = (SQLServerResultSet) psCastDateTime2DST.executeQuery();
            
            psCastDateTime2NotDST.setTimestamp(1, ts4);
            rsDateTime2NotDST = (SQLServerResultSet) psCastDateTime2NotDST.executeQuery();
            
            rsDTO.next();
            rsDate.next();
            rsSmallDateTime.next();
            rsDateTime.next();
            rsDateTime2.next();
            rsTime.next();
            
            rsDateTime2Pregregorian.next();
            rsDateTime2DST.next();
            rsDateTime2NotDST.next();
            
            psTable = (SQLServerPreparedStatement) conn.prepareStatement("INSERT INTO #tmp values (?)");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    // Date
    
    @Benchmark
    public void testGetDateAsDate(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDate));
    }
    
    @Benchmark
    public void testGetDateAsDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDate));
    }
//    
//    @Benchmark
//    public void testGetDateAsDTO(Blackhole bh) throws Exception {
//        bh.consume(testGetDTO(rsDate));
//    }
    
    @Benchmark
    public void testGetDateAsSmallDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDate));
    }
    
    @Benchmark
    public void testGetDateAsTimestamp(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDate));
    }
    
    @Benchmark
    public void testGetDateAsString(Blackhole bh) throws Exception {
        bh.consume(testGetString(rsDate));
    }
    
    @Benchmark
    public void testGetDateAsLocalDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetLocalDateTime(rsDate));
    }
    
    // Date with Cal
    
    @Benchmark
    public void testGetDateAsDateCal(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDate, cal));
    }
    
    @Benchmark
    public void testGetDateAsDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDate, cal));
    }
    
    @Benchmark
    public void testGetDateAsSmallDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDate, cal));
    }
    
    @Benchmark
    public void testGetDateAsTimestampCal(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDate, cal));
    }
    
    // DateTime
    
    @Benchmark
    public void testGetDateTimeAsDate(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDateTime));
    }
    
    @Benchmark
    public void testGetDateTimeAsDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDateTime));
    }
//    
//    @Benchmark
//    public void testGetDateTimeAsDTO(Blackhole bh) throws Exception {
//        bh.consume(testGetDTO(rsDateTime));
//    }
    
    @Benchmark
    public void testGetDateTimeAsSmallDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDateTime));
    }
    
    @Benchmark
    public void testGetDateTimeAsTimestamp(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDateTime));
    }
    
    @Benchmark
    public void testGetDateTimeAsString(Blackhole bh) throws Exception {
        bh.consume(testGetString(rsDateTime));
    }
    
    @Benchmark
    public void testGetDateTimeAsLocalDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetLocalDateTime(rsDateTime));
    }
    
    // DateTime with Cal
    
    @Benchmark
    public void testGetDateTimeAsDateCal(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDateTime, cal));
    }
    
    @Benchmark
    public void testGetDateTimeAsDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDateTime, cal));
    }
    
    @Benchmark
    public void testGetDateTimeAsSmallDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDateTime, cal));
    }
    
    @Benchmark
    public void testGetDateTimeAsTimestampCal(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDateTime, cal));
    }
    
    // DateTime2
    
    @Benchmark
    public void testGetDateTime2AsDate(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDateTime2));
    }
    
    @Benchmark
    public void testGetDateTime2AsDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDateTime2));
    }
//    
//    @Benchmark
//    public void testGetDateTime2AsDTO(Blackhole bh) throws Exception {
//        bh.consume(testGetDTO(rsDateTime2));
//    }
    
    @Benchmark
    public void testGetDateTime2AsSmallDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDateTime2));
    }
    
    @Benchmark
    public void testGetDateTime2AsTimestamp(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDateTime2));
    }
    
    @Benchmark
    public void testGetDateTime2AsString(Blackhole bh) throws Exception {
        bh.consume(testGetString(rsDateTime2));
    }
    
    @Benchmark
    public void testGetDateTime2AsLocalDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetLocalDateTime(rsDateTime2));
    }
    
    @Benchmark
    public void testGetDateTime2AsDateTimePregregorian(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDateTime2Pregregorian));
    }
    
    // DateTime2 with Cal
    
    @Benchmark
    public void testGetDateTime2AsDateCal(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDateTime2, cal));
    }
    
    @Benchmark
    public void testGetDateTime2AsDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDateTime2, cal));
    }
    
    @Benchmark
    public void testGetDateTime2AsSmallDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDateTime2, cal));
    }
    
    @Benchmark
    public void testGetDateTime2AsTimestampCal(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDateTime2, cal));
    }
    
    @Benchmark
    public void testGetDateTime2AsDateTimePregregorianCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDateTime2Pregregorian, cal));
    }
    
    // DTO
    
    @Benchmark
    public void testGetDTOAsDate(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDTO));
    }
    
    @Benchmark
    public void testGetDTOAsDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDTO));
    }
    
    @Benchmark
    public void testGetDTOAsDTO(Blackhole bh) throws Exception {
        bh.consume(testGetDTO(rsDTO));
    }
    
    @Benchmark
    public void testGetDTOAsSmallDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDTO));
    }
    
    @Benchmark
    public void testGetDTOAsTimestamp(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDTO));
    }
    
    @Benchmark
    public void testGetDTOAsString(Blackhole bh) throws Exception {
        bh.consume(testGetString(rsDTO));
    }
    
    @Benchmark
    public void testGetDTOAsLocalDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetLocalDateTime(rsDTO));
    }    
    
    // DTO with Cal
    
    @Benchmark
    public void testGetDTOAsDateCal(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsDTO, cal));
    }
    
    @Benchmark
    public void testGetDTOAsDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsDTO, cal));
    }
    
    @Benchmark
    public void testGetDTOAsSmallDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsDTO, cal));
    }
    
    @Benchmark
    public void testGetDTOAsTimestampCal(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsDTO, cal));
    }
    
    // SmallDateTime
    
    @Benchmark
    public void testGetSmallDateTimeAsDate(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsSmallDateTime));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsSmallDateTime));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsDTO(Blackhole bh) throws Exception {
        bh.consume(testGetDTO(rsSmallDateTime));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsSmallDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsSmallDateTime));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsTimestamp(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsSmallDateTime));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsString(Blackhole bh) throws Exception {
        bh.consume(testGetString(rsSmallDateTime));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsLocalDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetLocalDateTime(rsSmallDateTime));
    }
    
    // SmallDateTime with Cal
    
    @Benchmark
    public void testGetSmallDateTimeAsDateCal(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsSmallDateTime, cal));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsSmallDateTime, cal));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsSmallDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsSmallDateTime, cal));
    }
    
    @Benchmark
    public void testGetSmallDateTimeAsTimestampCal(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsSmallDateTime, cal));
    }
    
    // Time
    
    @Benchmark
    public void testGetTimeAsDate(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsTime));
    }
    
    @Benchmark
    public void testGetTimeAsDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsTime));
    }
    
    @Benchmark
    public void testGetTimeAsDTO(Blackhole bh) throws Exception {
        bh.consume(testGetDTO(rsTime));
    }
    
    @Benchmark
    public void testGetTimeAsSmallDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsTime));
    }
    
    @Benchmark
    public void testGetTimeAsTimestamp(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsTime));
    }
    
    @Benchmark
    public void testGetTimeAsString(Blackhole bh) throws Exception {
        bh.consume(testGetString(rsTime));
    }
    
    @Benchmark
    public void testGetTimeAsLocalDateTime(Blackhole bh) throws Exception {
        bh.consume(testGetLocalDateTime(rsTime));
    }
    
    // Time with Cal
    
    @Benchmark
    public void testGetTimeAsDateCal(Blackhole bh) throws Exception {
        bh.consume(testGetDate(rsTime, cal));
    }
    
    @Benchmark
    public void testGetTimeAsDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetDateTime(rsTime, cal));
    }
    
    @Benchmark
    public void testGetTimeAsSmallDateTimeCal(Blackhole bh) throws Exception {
        bh.consume(testGetSmallDateTime(rsTime, cal));
    }
    
    @Benchmark
    public void testGetTimeAsTimestampCal(Blackhole bh) throws Exception {
        bh.consume(testGetTimestamp(rsTime, cal));
    }
    
    
//    @Benchmark
//    public void testGetTimeAsTimestamp(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestamp(rsTime));
//    }
//    
//    @Benchmark
//    public void testGetDateTime2AsString(Blackhole bh) throws Exception {
//        bh.consume(testGetString(rsDateTime2));
//    }
//    
//    @Benchmark
//    public void testGetDateTime2AsTime(Blackhole bh) throws Exception {
//        bh.consume(testGetTime(rsDateTime2));
//    }
    
//    /**
//     * Tests scenario with set / execute / get, using timestamp from before Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testExecuteCastBeforeGregorian(Blackhole bh) throws Exception {
//        bh.consume(testExecuteCast(conn, psCast, ts));
//    }
//
//    /**
//     * Tests scenario with set / execute / get, using timestamp from before Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testExecuteTableBeforeGregorian(Blackhole bh) throws Exception {
//        bh.consume(testExecuteTable(conn, psTable, ts));
//    }
    
//    /**
//     * Tests scenario with set / execute / get, using timestamp from after Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testExecuteCastAfterGregorian(Blackhole bh) throws Exception {
//        bh.consume(testExecuteCast(conn, psCast, ts2));
//    }
//
//    /**
//     * Tests scenario with set / execute / get, using timestamp from after Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testExecuteTableAfterGregorian(Blackhole bh) throws Exception {
//        bh.consume(testExecuteTable(conn, psTable, ts2));
//    }
//
//    /**
//     * Tests scenario with set.
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testSetTimestamp(Blackhole bh) throws Exception {
//        bh.consume(testSetTimestamp());
//    }

//    /**
//     * Tests scenario with get, using timestamp from before Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDTOAsTimestampBeforeGregorian(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestamp(rsDTO));
//    }
//    
//    /**
//     * Tests scenario with get, using timestamp from after Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDTOAsTimestampAfterGregorian(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestamp(rsDTO2));
//    }


//    /**
//     * Tests scenario with set.
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testSetDTO(Blackhole bh) throws Exception {
//        bh.consume(testSetDTO());
//    }

//    /**
//     * Tests scenario with get, using timestamp from before Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDTOBeforeGregorian(Blackhole bh) throws Exception {
//        bh.consume(testGetDTO(rsDTO));
//    }
    
//    /**
//     * Tests scenario with get, using timestamp from after Gregorian Cutoff
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDTOAfterGregorian(Blackhole bh) throws Exception {
//        bh.consume(testGetDTO(rsDTO2));
//    }
    
//    /**
//     * Tests scenario with get, using timestamp within Daylight Savings Time
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDTOAsTimestampInDST(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestamp(rsDTO2));
//    }
//    
//    /**
//     * Tests scenario with get, using timestamp within Daylight Savings Time
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDateTime2AsTimestampInDST(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestamp(rsDateTime2DST));
//    }
//    
//    /**
//     * Tests scenario with get, using timestamp within Daylight Savings Time
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDateTime2AsTimestampInNotDST(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestamp(rsDateTime2NotDST));
//    }
//    
//    /**
//     * Tests scenario with get, using timestamp within Daylight Savings Time, with a Calendar from a different timezone
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDateTime2AsTimestampInDSTWithCalendar(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestampCalendar(rsDateTime2DST, cal));
//    }
//    
//    /**
//     * Tests scenario with get, using timestamp within Daylight Savings Time, with a Calendar from a different timezone
//     * 
//     * @param bh
//     */
//    @Benchmark
//    public void testGetDateTime2AsTimestampInNotDSTWithCalendar(Blackhole bh) throws Exception {
//        bh.consume(testGetTimestampCalendar(rsDateTime2NotDST, cal));
//    }

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
        psCastDTO.setTimestamp(1, ts);
        return 0;
    }

    private int testSetDTO() throws Exception {
        psCastDTO.setDateTimeOffset(1, dto);
        return 0;
    }
    
    private DateTimeOffset testGetDTO(SQLServerResultSet rs) throws Exception {
        return rs.getDateTimeOffset(1);
    }
    
    private java.sql.Date testGetDate(SQLServerResultSet rs, Calendar cal) throws Exception {
        return rs.getDate(1, cal);
    }
    
    private java.sql.Date testGetDate(SQLServerResultSet rs) throws Exception {
        return rs.getDate(1);
    }
    
    private java.sql.Timestamp testGetDateTime(SQLServerResultSet rs) throws Exception {
        return rs.getDateTime(1);
    }
    
    private java.sql.Timestamp testGetDateTime(SQLServerResultSet rs, Calendar cal) throws Exception {
        return rs.getDateTime(1, cal);
    }
    
    private java.sql.Timestamp testGetSmallDateTime(SQLServerResultSet rs) throws Exception {
        return rs.getSmallDateTime(1);
    }
    
    private java.sql.Timestamp testGetSmallDateTime(SQLServerResultSet rs, Calendar cal) throws Exception {
        return rs.getSmallDateTime(1, cal);
    }
    
    private java.sql.Time testGetTime(SQLServerResultSet rs) throws Exception {
        return rs.getTime(1);
    }
    
    private Timestamp testGetTimestamp(SQLServerResultSet rs) throws Exception {
        return rs.getTimestamp(1);
    }
    
    private Timestamp testGetTimestamp(SQLServerResultSet rs, Calendar cal) throws Exception {
        return rs.getTimestamp(1, cal);
    }
    
    private String testGetString(SQLServerResultSet rs) throws Exception {
        return rs.getString(1);
    }
    
    private LocalDateTime testGetLocalDateTime(SQLServerResultSet rs) throws Exception {
        return rs.getObject(1, LocalDateTime.class);
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
