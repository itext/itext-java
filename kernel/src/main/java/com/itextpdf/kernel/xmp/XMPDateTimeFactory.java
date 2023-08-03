//Copyright (c) 2006, Adobe Systems Incorporated
//All rights reserved.
//
//        Redistribution and use in source and binary forms, with or without
//        modification, are permitted provided that the following conditions are met:
//        1. Redistributions of source code must retain the above copyright
//        notice, this list of conditions and the following disclaimer.
//        2. Redistributions in binary form must reproduce the above copyright
//        notice, this list of conditions and the following disclaimer in the
//        documentation and/or other materials provided with the distribution.
//        3. All advertising materials mentioning features or use of this software
//        must display the following acknowledgement:
//        This product includes software developed by the Adobe Systems Incorporated.
//        4. Neither the name of the Adobe Systems Incorporated nor the
//        names of its contributors may be used to endorse or promote products
//        derived from this software without specific prior written permission.
//
//        THIS SOFTWARE IS PROVIDED BY ADOBE SYSTEMS INCORPORATED ''AS IS'' AND ANY
//        EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//        WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//        DISCLAIMED. IN NO EVENT SHALL ADOBE SYSTEMS INCORPORATED BE LIABLE FOR ANY
//        DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//        (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//        LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//        ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//        (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//        http://www.adobe.com/devnet/xmp/library/eula-xmp-library-java.html

package com.itextpdf.kernel.xmp;

import com.itextpdf.kernel.xmp.impl.XMPDateTimeImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A factory to create <code>XMPDateTime</code>-instances from a <code>Calendar</code> or an
 * ISO 8601 string or for the current time.
 * 
 * @since 16.02.2006
 */
public final class XMPDateTimeFactory
{
	/** The UTC TimeZone */
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	

	/** Private constructor */
	private XMPDateTimeFactory()
	{
		// EMPTY
	}


	/**
	 * Creates an <code>XMPDateTime</code> from a <code>Calendar</code>-object.
	 * 
	 * @param calendar a <code>Calendar</code>-object.
	 * @return An <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime createFromCalendar(Calendar calendar)
	{
		return new XMPDateTimeImpl(calendar);
	}

	
	/**
	 * Creates an empty <code>XMPDateTime</code>-object.
	 * @return Returns an <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime create()
	{
		return new XMPDateTimeImpl();
	}
	
	
	/**
	 * Creates an <code>XMPDateTime</code>-object from initial values.
	 * @param year years
	 * @param month months from 1 to 12<br>
	 * <em>Note:</em> Remember that the month in {@link java.util.Calendar} is defined from 0 to 11.
	 * @param day days
	 * @return Returns an <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime create(int year, int month, int day)
	{
		XMPDateTime dt = new XMPDateTimeImpl();
		dt.setYear(year);
		dt.setMonth(month);
		dt.setDay(day);
		return dt;
	}


	/**
	 * Creates an <code>XMPDateTime</code>-object from initial values.
	 * @param year years
	 * @param month months from 1 to 12<br>
	 * <em>Note:</em> Remember that the month in {@link java.util.Calendar} is defined from 0 to 11.
	 * @param day days
	 * @param hour hours
	 * @param minute minutes
	 * @param second seconds
	 * @param nanoSecond nanoseconds 
	 * @return Returns an <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime create(int year, int month, int day, 
		int hour, int minute, int second, int nanoSecond)
	{
		XMPDateTime dt = new XMPDateTimeImpl();
		dt.setYear(year);
		dt.setMonth(month);
		dt.setDay(day);
		dt.setHour(hour);
		dt.setMinute(minute);
		dt.setSecond(second);
		dt.setNanoSecond(nanoSecond);
		return dt;
	}
	

	/**
	 * Creates an <code>XMPDateTime</code> from an ISO 8601 string.
	 * 
	 * @param strValue The ISO 8601 string representation of the date/time.
	 * @return An <code>XMPDateTime</code>-object.
	 * @throws XMPException When the ISO 8601 string is non-conform
	 */
	public static XMPDateTime createFromISO8601(String strValue) throws XMPException
	{
		return new XMPDateTimeImpl(strValue);
	}


	/**
	 * Obtain the current date and time.
	 * 
	 * @return Returns The returned time is UTC, properly adjusted for the local time zone. The
	 *         resolution of the time is not guaranteed to be finer than seconds.
	 */
	public static XMPDateTime getCurrentDateTime()
	{
		return new XMPDateTimeImpl(new GregorianCalendar());
	}


	/**
	 * Sets the local time zone without touching any other Any existing time zone value is replaced,
	 * the other date/time fields are not adjusted in any way.
	 * 
	 * @param dateTime the <code>XMPDateTime</code> variable containing the value to be modified.
	 * @return Returns an updated <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime setLocalTimeZone(XMPDateTime dateTime)
	{
		Calendar cal = dateTime.getCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		return new XMPDateTimeImpl(cal);
	}


	/**
	 * Make sure a time is UTC. If the time zone is not UTC, the time is
	 * adjusted and the time zone set to be UTC.
	 * 
	 * @param dateTime
	 *            the <code>XMPDateTime</code> variable containing the time to
	 *            be modified.
	 * @return Returns an updated <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime convertToUTCTime(XMPDateTime dateTime)
	{
		long timeInMillis = dateTime.getCalendar().getTimeInMillis();
		GregorianCalendar cal = new GregorianCalendar(UTC);
		cal.setGregorianChange(new Date(Long.MIN_VALUE));		
		cal.setTimeInMillis(timeInMillis);
		return new XMPDateTimeImpl(cal);
	}


	/**
	 * Make sure a time is local. If the time zone is not the local zone, the time is adjusted and
	 * the time zone set to be local.
	 * 
	 * @param dateTime the <code>XMPDateTime</code> variable containing the time to be modified.
	 * @return Returns an updated <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime convertToLocalTime(XMPDateTime dateTime)
	{
		long timeInMillis = dateTime.getCalendar().getTimeInMillis();
		// has automatically local timezone
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTimeInMillis(timeInMillis);
		return new XMPDateTimeImpl(cal);
	}
}