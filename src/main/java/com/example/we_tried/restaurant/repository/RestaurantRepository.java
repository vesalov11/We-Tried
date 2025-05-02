package com.example.we_tried.restaurant.repository;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.model.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID>{

    Optional<Restaurant> findByName(String name);

    List<Restaurant> findByRestaurantType(RestaurantType type);


    List<Restaurant> findByNameContainingIgnoreCase(String query);

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.dishes WHERE r.id = :id")
    Optional<Restaurant> findByIdWithDishes(UUID id);
}