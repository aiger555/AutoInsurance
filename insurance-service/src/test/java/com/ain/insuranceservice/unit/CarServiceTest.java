
package com.ain.insuranceservice.unit;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.exception.CarNotFoundException;
import com.ain.insuranceservice.exception.VinAlreadyExistsException;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.VehicleType;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private CarRequestDTO requestDTO;
    private UUID carId;
    private Client owner;

    @BeforeEach
    void setUp() {
        carId = UUID.randomUUID();

        owner = new Client();
        owner.setId(UUID.randomUUID());
        owner.setFullName("Ivan Ivanov");

        testCar = new Car();
        testCar.setId(carId);
        testCar.setBrand("Toyota");
        testCar.setModel("Camry");
        testCar.setManufactureYear(2020);
        testCar.setVin("JTDKB20U303000001");
        testCar.setLicensePlate("01KG123AB");
        testCar.setRegistrationAuthority("GVDD");
        testCar.setRegistrationDate(LocalDate.of(2020, 5, 15));
        testCar.setTechPassportNumber("TP123456");
        testCar.setEngineVolume(2.5);
        testCar.setVehicleType(VehicleType.PASSENGER_CAR);
        testCar.setOwner(owner);

        requestDTO = new CarRequestDTO();
        requestDTO.setBrand("Toyota");
        requestDTO.setModel("Camry");
        requestDTO.setManufactureYear("2020");
        requestDTO.setVin("JTDKB20U303000001");
        requestDTO.setLicensePlate("01KG123AB");
        requestDTO.setRegistrationAuthority("GVDD");
        requestDTO.setRegistrationDate("2020-05-15");
        requestDTO.setTechPassportNumber("TP123456");
        requestDTO.setEngineVolume("2.5");
        requestDTO.setVehicleType(VehicleType.PASSENGER_CAR);
        requestDTO.setOwner(owner);
    }

    @Test
    void createCar_ValidData_ReturnsCar() {
        when(carRepository.existsByVin(requestDTO.getVin())).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        CarResponseDTO result = carService.createCar(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getBrand()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
    }

    @Test
    void createCar_DuplicateVin_ThrowsException() {
        when(carRepository.existsByVin(requestDTO.getVin())).thenReturn(true);

        assertThatThrownBy(() -> carService.createCar(requestDTO))
                .isInstanceOf(VinAlreadyExistsException.class);
    }

    @Test
    void getCars_ReturnsAllCars() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findAll()).thenReturn(cars);

        List<CarResponseDTO> result = carService.getCars();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    void updateCar_ValidData_ReturnsUpdatedCar() {
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(carRepository.existsByVinAndIdNot(requestDTO.getVin(), carId)).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        CarResponseDTO result = carService.updateCar(carId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(carId.toString());
    }

    @Test
    void updateCar_CarNotFound_ThrowsException() {
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.updateCar(carId, requestDTO))
                .isInstanceOf(CarNotFoundException.class);
    }

    @Test
    void deleteCar_ValidId_DeletesCar() {
        doNothing().when(carRepository).deleteById(carId);

        carService.deleteCar(carId);

        verify(carRepository).deleteById(carId);
    }
}