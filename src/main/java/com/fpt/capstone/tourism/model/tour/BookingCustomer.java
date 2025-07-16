package com.fpt.capstone.tourism.model.tour;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.enums.Gender;
import com.fpt.capstone.tourism.model.enums.PaxType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booking_customer")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BookingCustomer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Booking booking;

    @Column(name = "customer_name")
    private String fullName;

    private String address;

    private String email;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "pick_up_address")
    private String pickUpAddress;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "pax_type")
    @Enumerated(EnumType.STRING)
    private PaxType paxType;

    @Column(name = "single_room")
    private boolean singleRoom;

    @Column(name = "booked_person")
    private boolean bookedPerson;
}
