package com.example.trackingmypantry;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"productId","userId"})
public class Product {

    public Product() {
    }

    @ColumnInfo
    @NonNull
    public String productId;

    @ColumnInfo
    @NonNull
    public String userId; //email

    @ColumnInfo
    public String barcode;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String description;

    @ColumnInfo
    public Integer quantity;

    @ColumnInfo
    public String image;

}