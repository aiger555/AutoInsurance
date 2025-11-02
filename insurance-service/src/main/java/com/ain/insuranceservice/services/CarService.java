package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.exception.CarNotFoundException;
import com.ain.insuranceservice.exception.ClientNotFoundException;
import com.ain.insuranceservice.exception.PinAlreadyExistsException;
import com.ain.insuranceservice.exception.VinAlreadyExistsException;
import com.ain.insuranceservice.mappers.CarMapper;
import com.ain.insuranceservice.mappers.ClientMapper;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.repositories.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {
    private CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarResponseDTO> getCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(CarMapper::toDTO).toList();
    }

    public CarResponseDTO createCar(CarRequestDTO carRequestDTO) {
        if (carRepository.existsByVin(carRequestDTO.getVin())) {
            throw new VinAlreadyExistsException("A car with this vin already exists" + carRequestDTO.getVin());
        }
        Car newCar = carRepository.save(CarMapper.toModel(carRequestDTO));
        return CarMapper.toDTO(newCar);
    }

    public CarResponseDTO updateCar(UUID id, CarRequestDTO carRequestDTO) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new CarNotFoundException("Car not found with ID: " + id));

        if (carRepository.existsByVinAndIdNot(carRequestDTO.getVin(), id)) {
            throw new VinAlreadyExistsException("A car with this vin already exists" + carRequestDTO.getVin());
        }

        car.setBrand(carRequestDTO.getBrand());
        car.setModel(carRequestDTO.getModel());
        car.setManufactureYear(Integer.parseInt(carRequestDTO.getManufactureYear()));
        car.setVin(carRequestDTO.getVin());
        car.setLicensePlate(carRequestDTO.getLicensePlate());
        car.setRegistrationAuthority(carRequestDTO.getRegistrationAuthority());
        car.setRegistrationDate(LocalDate.parse(carRequestDTO.getRegistrationDate()));
        car.setTechPassportNumber(carRequestDTO.getTechPassportNumber());

        car.setEngineVolume(safeParseDouble(carRequestDTO.getEngineVolume()));
        car.setMaxAllowedWeight(safeParseDouble(carRequestDTO.getMaxAllowedWeight()));
        car.setBatteryCapacity(safeParseDouble(carRequestDTO.getBatteryCapacity()));
        car.setPassengerCapacity(safeParseDouble(carRequestDTO.getPassengerCapacity()));

        car.setVehicleType(carRequestDTO.getVehicleType());
        car.setOwner(carRequestDTO.getOwner());
        Car updatedCar = carRepository.save(car);
        return CarMapper.toDTO(updatedCar);
    }

    private Double safeParseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
