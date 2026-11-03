
package com.coworking.hotdesk.service;

import com.coworking.hotdesk.dao.BookingDao;
import com.coworking.hotdesk.dao.UserDao;
import com.coworking.hotdesk.dao.WorkspaceDao;
import com.coworking.hotdesk.entity.Booking;
import com.coworking.hotdesk.entity.User;
import com.coworking.hotdesk.entity.Workspace;
import com.coworking.hotdesk.service.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingDao bookingDao;

    @Mock
    private UserDao userDao;

    @Mock
    private WorkspaceDao workspaceDao;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Workspace workspace;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "Gauri Damare",
                "gauri@gmail.com",
                new BigDecimal("500.00")
        );

        workspace = new Workspace(
                1L,
                "Desk-A10",
                "HOT_DESK",
                new BigDecimal("50.00"),
                true
        );
    }

    @Test
    void successfulBookingShouldDeductWalletBalance() {

        Booking booking = new Booking();
        booking.setUserId(1L);
        booking.setSpaceId(1L);
        booking.setBookingDate(LocalDate.now().plusDays(2));
        booking.setSlotHour(10);

        when(userDao.getUserById(1L)).thenReturn(user);
        when(workspaceDao.getWorkspaceById(1L)).thenReturn(workspace);
        when(bookingDao.isSlotAvailable(
                1L, booking.getBookingDate(), 10
        )).thenReturn(true);

        bookingService.createBooking(booking);

        // assertEquals(
        //         new BigDecimal("450.00"),
        //         user.getWalletBalance()
        // );

        verify(userDao).updateWalletBalance(
        1L, new BigDecimal("450.00")
        );

        assertEquals(
                new BigDecimal("50.00"),
                booking.getTotalCost()
        );

        assertEquals("CONFIRMED", booking.getBookingStatus());

        verify(userDao).updateWalletBalance(
                1L, new BigDecimal("450.00")
        );

        verify(bookingDao).addBooking(booking);
    }

    @Test
    void insufficientBalanceShouldRejectBooking() {

        user.setWalletBalance(new BigDecimal("20.00"));

        Booking booking = new Booking();
        booking.setUserId(1L);
        booking.setSpaceId(1L);
        booking.setBookingDate(LocalDate.now().plusDays(2));
        booking.setSlotHour(10);

        when(userDao.getUserById(1L)).thenReturn(user);
        when(workspaceDao.getWorkspaceById(1L)).thenReturn(workspace);
        when(bookingDao.isSlotAvailable(
                1L, booking.getBookingDate(), 10
        )).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(booking)
        );

        verify(userDao, never()).updateWalletBalance(
                anyLong(), any(BigDecimal.class)
        );

        verify(bookingDao, never()).addBooking(any(Booking.class));
    }

    @Test
    void duplicateBookingShouldBeRejected() {

        Booking booking = new Booking();
        booking.setUserId(1L);
        booking.setSpaceId(1L);
        booking.setBookingDate(LocalDate.now().plusDays(2));
        booking.setSlotHour(10);

        when(userDao.getUserById(1L)).thenReturn(user);
        when(workspaceDao.getWorkspaceById(1L)).thenReturn(workspace);
        when(bookingDao.isSlotAvailable(
                1L, booking.getBookingDate(), 10
        )).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(booking)
        );

        verify(userDao, never()).updateWalletBalance(
                anyLong(), any(BigDecimal.class)
        );

        verify(bookingDao, never()).addBooking(any(Booking.class));
    }

    @Test
    void pastDateBookingShouldBeRejected() {

        Booking booking = new Booking();
        booking.setUserId(1L);
        booking.setSpaceId(1L);
        booking.setBookingDate(LocalDate.now().minusDays(1));
        booking.setSlotHour(10);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(booking)
        );

        verifyNoInteractions(userDao, workspaceDao, bookingDao);
    }

    @Test
    void inactiveWorkspaceShouldBeRejected() {

        workspace.setActive(false);

        Booking booking = new Booking();
        booking.setUserId(1L);
        booking.setSpaceId(1L);
        booking.setBookingDate(LocalDate.now().plusDays(2));
        booking.setSlotHour(10);

        when(userDao.getUserById(1L)).thenReturn(user);
        when(workspaceDao.getWorkspaceById(1L)).thenReturn(workspace);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(booking)
        );

        verify(bookingDao, never()).addBooking(any(Booking.class));
    }

    
    @Test
    void successfulCancellationShouldRefundWallet() {

        Booking booking = new Booking(
                1L,
                1L,
                1L,
                LocalDate.now().plusDays(2),
                10,
                new BigDecimal("50.00"),
                "CONFIRMED"
        );

        when(bookingDao.getBookingById(1L)).thenReturn(booking);
        when(bookingDao.cancelConfirmedBooking(1L)).thenReturn(1);
        when(userDao.getUserById(1L)).thenReturn(user);

        bookingService.cancelBooking(1L);

        verify(bookingDao).cancelConfirmedBooking(1L);

        verify(userDao).updateWalletBalance(
                1L,
                new BigDecimal("550.00")
        );
    }

    @Test
    void alreadyCancelledBookingShouldBeRejected() {

        Booking booking = new Booking(
                1L,
                1L,
                1L,
                LocalDate.now().plusDays(2),
                10,
                new BigDecimal("50.00"),
                "CANCELLED"
        );

        when(bookingDao.getBookingById(1L)).thenReturn(booking);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking(1L)
        );

        verify(bookingDao, never()).cancelConfirmedBooking(1L);

        verify(userDao, never()).updateWalletBalance(
                anyLong(), any(BigDecimal.class)
        );
    }

    @Test
    void pastDateCancellationShouldBeRejected() {

        Booking booking = new Booking(
                1L,
                1L,
                1L,
                LocalDate.now().minusDays(1),
                10,
                new BigDecimal("50.00"),
                "CONFIRMED"
        );

        when(bookingDao.getBookingById(1L)).thenReturn(booking);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking(1L)
        );

        verify(bookingDao, never()).cancelConfirmedBooking(1L);

        verify(userDao, never()).updateWalletBalance(
                anyLong(), any(BigDecimal.class)
        );
    }

    @Test
    void missingBookingIdShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking(null)
        );

        verifyNoInteractions(bookingDao, userDao, workspaceDao);
    }

    @Test
void missingUserShouldBeRejected() {

    Booking booking = new Booking();
    booking.setUserId(99L);
    booking.setSpaceId(1L);
    booking.setBookingDate(LocalDate.now().plusDays(2));
    booking.setSlotHour(10);

    when(userDao.getUserById(99L)).thenReturn(null);

    assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.createBooking(booking)
    );

    verify(workspaceDao, never()).getWorkspaceById(anyLong());
    verify(bookingDao, never()).addBooking(any(Booking.class));
}

@Test
void missingWorkspaceShouldBeRejected() {

    Booking booking = new Booking();
    booking.setUserId(1L);
    booking.setSpaceId(99L);
    booking.setBookingDate(LocalDate.now().plusDays(2));
    booking.setSlotHour(10);

    when(userDao.getUserById(1L)).thenReturn(user);
    when(workspaceDao.getWorkspaceById(99L)).thenReturn(null);

    assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.createBooking(booking)
    );

    verify(bookingDao, never()).addBooking(any(Booking.class));
    verify(userDao, never()).updateWalletBalance(
            anyLong(), any(BigDecimal.class)
    );
}

}
