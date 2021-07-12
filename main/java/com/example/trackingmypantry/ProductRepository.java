package com.example.trackingmypantry;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ProductRepository {

    private ProductDao mProductDao;
    private LiveData<List<Product>> mListOfProduct;

    ProductRepository (Application app, String userId) {
        AppDatabaseProduct db = AppDatabaseProduct.getDatabase(app);
        mProductDao = db.productDao();
        mListOfProduct = mProductDao.getListOfProducts(userId);
    }

    LiveData<List<Product>> getAllProducts(){
        return mListOfProduct;
    }

    void insert(Product product){
        AppDatabaseProduct.databaseWriteExecutor.execute(() -> {
            List<Product> productIfExist = mProductDao.getProduct(product.userId, product.productId);
            if (productIfExist.isEmpty()) {
                mProductDao.insertProduct(product);
            } else {
                Product productToUpdate = productIfExist.get(0);
                mProductDao.updateProduct(product.userId, productToUpdate.productId, 1);
            }
        });
    }

    void deleteProduct(Product product){
        AppDatabaseProduct.databaseWriteExecutor.execute(() -> {
            mProductDao.deleteProduct(product.userId, product.productId);
        });
    }

    void removeQuantityProduct(Product product){
        AppDatabaseProduct.databaseWriteExecutor.execute(() -> {
            mProductDao.modifyQuantity(product.userId, product.productId);
        });
    }

    void addQuantityProduct(Product product){
        AppDatabaseProduct.databaseWriteExecutor.execute(() -> {
            mProductDao.updateProduct(product.userId, product.productId, 1);
        });
    }

    /*
    void printDatabase(){
        AppDatabaseProduct.databaseWriteExecutor.execute(() -> {
            List<Product> myList = mProductDao.getAll();
            for (Product product:myList) {
                Log.i("INSERT PRODUCT", product.productId + "\t" + product.barcode + "\t" + product.name + "\t" + product.description + "\t" + product.quantity);
            }
        });
    }
    */

}
