package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.AddressRequest;
import com.ilbarslab.ardbackend.print.dto.response.AddressResponse;
import com.ilbarslab.ardbackend.print.entity.Address;
import com.ilbarslab.ardbackend.print.entity.User;
import com.ilbarslab.ardbackend.print.repository.AddressRepository;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressResponse> getAddresses(String email) {
        User user = getUser(email);
        return addressRepository.findByUserIdOrderByIsDefaultDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AddressResponse addAddress(String email, AddressRequest request) {
        User user = getUser(email);

        // Eğer default işaretlendiyse diğerlerini kaldır
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(a -> { a.setIsDefault(false); addressRepository.save(a); });
        }

        Address address = Address.builder()
                .user(user)
                .title(request.getTitle())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .addressLine(request.getAddressLine())
                .district(request.getDistrict())
                .city(request.getCity())
                .country(request.getCountry() != null ? request.getCountry() : "Türkiye")
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(String email, UUID id, AddressRequest request) {
        User user = getUser(email);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adres bulunamadı"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(a -> { a.setIsDefault(false); addressRepository.save(a); });
        }

        address.setTitle(request.getTitle());
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, UUID id) {
        User user = getUser(email);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adres bulunamadı"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }
        addressRepository.delete(address);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    private AddressResponse toResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .addressLine(a.getAddressLine())
                .district(a.getDistrict())
                .city(a.getCity())
                .country(a.getCountry())
                .isDefault(a.getIsDefault())
                .build();
    }
}
