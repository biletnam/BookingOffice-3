package com.kukushkin.booking.office.services;

import com.kukushkin.booking.office.dao.FlightDao;
import com.kukushkin.booking.office.dao.ReservationDao;
import com.kukushkin.booking.office.dao.TicketDao;
import com.kukushkin.booking.office.entity.Flight;
import com.kukushkin.booking.office.entity.Reservation;
import com.kukushkin.booking.office.entity.Ticket;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;


public class ReservationManagementService {
    private ReservationDao reservationDao;
    private TicketDao ticketDao;
    private FlightDao flightDao;

    public void createReservation(Reservation reservation, Ticket... tickets) {
        try {
            reservationDao.add(reservation);
            for (Ticket ticket : tickets) {
                ticket.setReservationId(reservation.getId());
                ticket.setStatus(TicketDao.Status.BOOKED);
                ticketDao.update(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO: refactor
    public void acceptPayment(Reservation reservation) {
        try {
            reservationDao.update(reservation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Flight> findFlightsByCriteria(Date departureDate, String destination) {
        List<Flight> flights = null;
        try {
            flights = flightDao.findFlights(departureDate, destination);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public List<Reservation> getBookedReservations() {
        List<Reservation> reservationList = null;
        try {
            reservationList = reservationDao.findNonExpired();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationList;
    }

    public void sellTickets(Reservation reservation) {
        try {
            reservation.setPaymentDate(new java.sql.Date(new java.util.Date().getTime()));
            reservationDao.update(reservation);
            for (Ticket ticket : ticketDao.getTicketsForReservation(reservation.getId())) {
                ticket.setStatus(TicketDao.Status.SOLD);
                ticketDao.update(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
