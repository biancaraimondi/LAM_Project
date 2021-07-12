package com.example.trackingmypantry;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product WHERE productId LIKE :productId AND userId = :userId")
    List<Product> getProduct(String userId, String productId);

    @Query("UPDATE product SET quantity = (quantity + :productQuantity) WHERE productId = :productId AND userId = :userId")
    int updateProduct(String userId, String productId, int productQuantity);

    @Query("UPDATE product SET quantity = (quantity - 1) WHERE productId = :productId AND userId = :userId AND quantity>1")
    void modifyQuantity(String userId, String productId);

    @Insert
    void insertProduct(Product product);

    @Query("DELETE FROM product WHERE productId = :productId AND userId = :userId")
    void deleteProduct(String userId, String productId);

    @Query("DELETE FROM product")
    void deleteAll();

    @Query("SELECT * FROM product WHERE userId = :userId")
    LiveData<List<Product>> getListOfProducts(String userId);
}