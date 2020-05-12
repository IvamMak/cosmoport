package com.space.controller;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class ShipController {

    @Autowired
    private ShipService shipService;

    @GetMapping(path = "/ships")
    public List<Ship> findAll(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "3", required = false) int pageSize,
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "planet", defaultValue = "", required = false) String planet,
            @RequestParam(name = "shipType", defaultValue = "", required = false) String shipType,
            @RequestParam(name = "after", defaultValue = "0", required = false) long after,
            @RequestParam(name = "before", defaultValue = "9223372036854775807", required = false)
                    long before,
            @RequestParam(name = "isUsed", defaultValue = "", required = false) String isUsed,
            @RequestParam(name = "minSpeed", defaultValue = "0.01", required = false) double minSpeed,
            @RequestParam(name = "maxSpeed", defaultValue = "0.99", required = false) double maxSpeed,
            @RequestParam(name = "minCrewSize", defaultValue = "1", required = false) int minCrewSize,
            @RequestParam(name = "maxCrewSize", defaultValue = "9999", required = false) int maxCrewSize,
            @RequestParam(name = "minRating", defaultValue = "0.0", required = false) double minRating,
            @RequestParam(name = "maxRating", defaultValue = "100000", required = false) double maxRating,
            @RequestParam(name = "order", defaultValue = "ID", required = false) String order) {

        List<Ship> list;
        int index = pageNumber * pageSize;
        list = shipService.getAll().stream()
                .filter(ship -> ship.getName().contains(name))
                .filter(ship -> ship.getPlanet().contains(planet))
                .filter(ship -> ship.getShipType().name().contains(shipType))
                .filter(ship -> ship.getProdDate().getTime() >= after &&
                        ship.getProdDate().getTime() <= before)
                .filter(ship -> ship.getUsed().toString().contains(isUsed))
                .filter(ship -> ship.getSpeed() >= minSpeed && ship.getSpeed() <= maxSpeed)
                .filter(ship -> ship.getCrewSize() >= minCrewSize && ship.getCrewSize() <= maxCrewSize)
                .filter(ship -> ship.getRating() >= minRating && ship.getRating() <= maxRating)
                .collect(Collectors.toList());

        list.sort((o1, o2) -> {
            if (order.equals("SPEED"))
                return Double.compare(o1.getSpeed(), o2.getSpeed());
            else if (order.equals("DATE"))
                return Long.compare(o1.getProdDate().getTime(), o2.getProdDate().getTime());
            else if (order.equals("RATING"))
                return Double.compare(o1.getRating(), o2.getRating());
            else return Long.compare(o1.getId(), o2.getId());
        });

        if ((index + pageSize) <= list.size()) return list.subList(index, index + pageSize);
        else return list.subList(index, list.size());
    }

    @PostMapping(path = "/ships")
    public Ship createNewShip(@RequestBody Ship requestShip) throws BadRequestException {
        String name = requestShip.getName();
        String planet = requestShip.getPlanet();
        ShipType shipType = requestShip.getShipType();
        Date prodDate = requestShip.getProdDate();
        Double speed = requestShip.getSpeed();
        Integer crewSize = requestShip.getCrewSize();

        if (name == null || planet == null ||
                name.isEmpty() || planet.isEmpty() ||
                shipType == null || prodDate.getTime() == 0 ||
                speed == null || crewSize == null) throw new BadRequestException();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2800, Calendar.DECEMBER, 31);
        long firstDate = calendar.getTimeInMillis();

        calendar.set(3019, Calendar.DECEMBER, 31);
        long secondDate = calendar.getTimeInMillis();

        if (name.length() > 50 || planet.length() > 50 ||
                speed < 0.01 || speed > 0.99 ||
                crewSize < 1 || crewSize > 9999 ||
                prodDate.getTime() < firstDate || prodDate.getTime() > secondDate) throw new BadRequestException();

        if (requestShip.getUsed() == null) requestShip.setUsed(false);
        requestShip.setRating(requestShip.calcRating(requestShip.getUsed(), speed, prodDate));

        shipService.save(requestShip);

        return requestShip;
    }

    @GetMapping(path = "/ships/count")
    public int getShipsCount(
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "planet", defaultValue = "", required = false) String planet,
            @RequestParam(name = "shipType", defaultValue = "", required = false) String shipType,
            @RequestParam(name = "after", defaultValue = "0", required = false) long after,
            @RequestParam(name = "before", defaultValue = "9223372036854775807", required = false)
                    long before,
            @RequestParam(name = "isUsed", defaultValue = "", required = false) String isUsed,
            @RequestParam(name = "minSpeed", defaultValue = "0.01", required = false) double minSpeed,
            @RequestParam(name = "maxSpeed", defaultValue = "0.99", required = false) double maxSpeed,
            @RequestParam(name = "minCrewSize", defaultValue = "1", required = false) int minCrewSize,
            @RequestParam(name = "maxCrewSize", defaultValue = "9999", required = false) int maxCrewSize,
            @RequestParam(name = "minRating", defaultValue = "0.0", required = false) double minRating,
            @RequestParam(name = "maxRating", defaultValue = "100000", required = false) double maxRating) {
        return (int) shipService.getAll().stream()
                .filter(ship -> ship.getName().contains(name))
                .filter(ship -> ship.getPlanet().contains(planet))
                .filter(ship -> ship.getShipType().name().contains(shipType))
                .filter(ship -> ship.getProdDate().getTime() >= after &&
                        ship.getProdDate().getTime() <= before)
                .filter(ship -> ship.getUsed().toString().contains(isUsed))
                .filter(ship -> ship.getSpeed() >= minSpeed && ship.getSpeed() <= maxSpeed)
                .filter(ship -> ship.getCrewSize() >= minCrewSize && ship.getCrewSize() <= maxCrewSize)
                .filter(ship -> ship.getRating() >= minRating && ship.getRating() <= maxRating).count();
    }

    @GetMapping(value = "/ships/{id}")
    public Ship getShipById(@PathVariable long id) throws NotFoundException, BadRequestException {
        if (id == 0) throw new BadRequestException();
        return shipService.getAll().stream()
                .filter(ship -> ship.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping(path = "/ships/{id}")
    public Ship updateShip(@PathVariable Long id, @RequestBody Ship requestShip)
            throws BadRequestException, NotFoundException {
        String name = requestShip.getName();
        String planet = requestShip.getPlanet();
        ShipType shipType = requestShip.getShipType();
        Date prodDate = requestShip.getProdDate();
        Double speed = requestShip.getSpeed();
        Integer crewSize = requestShip.getCrewSize();
        Boolean isUsed = requestShip.getUsed();

        if (id == 0) throw new BadRequestException();

        Ship shipFromDB = shipService.getAll().stream()
                .filter(ship -> ship.getId().equals(id)).findFirst().orElseThrow(NotFoundException::new);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2800, Calendar.DECEMBER, 31);
        long firstDate = calendar.getTimeInMillis();

        calendar.set(3019, Calendar.DECEMBER, 31);
        long secondDate = calendar.getTimeInMillis();
        if (name != null) {
            if (name.length() < 1 || name.length() > 50) throw new BadRequestException();
            else shipFromDB.setName(name);
        }

        if (planet != null) {
            if (planet.length() < 1 || planet.length() > 50) throw new BadRequestException();
            else shipFromDB.setPlanet(planet);
        }

        if (prodDate != null) {
            if (prodDate.getTime() < firstDate || prodDate.getTime() > secondDate) throw new BadRequestException();
            else shipFromDB.setProdDate(prodDate);
        }

        if (speed != null) {
            if (speed < 0.01 || speed > 0.99) throw new BadRequestException();
            else shipFromDB.setSpeed(speed);
        }

        if (crewSize != null) {
            if (crewSize < 1 || crewSize > 9999) throw new BadRequestException();
            else shipFromDB.setCrewSize(crewSize);
        }

        if (isUsed != null) shipFromDB.setUsed(isUsed);
        if (shipType != null) shipFromDB.setShipType(shipType);

        shipFromDB.setRating(shipFromDB.calcRating(shipFromDB.getUsed(), shipFromDB.getSpeed(), shipFromDB.getProdDate()));

        Ship newShip = shipFromDB;

        shipService.delete(shipFromDB.getId());
        shipService.save(newShip);
        return newShip;
    }

    @DeleteMapping(path = "/ships/{id}")
    public void deleteShipByID(@PathVariable Long id)
            throws BadRequestException, NotFoundException {
        if (id == 0) throw new BadRequestException();

        shipService.getAll().stream()
                .filter(ship -> ship.getId() == id)
                .findFirst().orElseThrow(NotFoundException::new);

        shipService.delete(id);
    }
}

