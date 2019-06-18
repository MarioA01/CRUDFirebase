package app.m.crudfirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.m.crudfirebase.models.Producto;

public class MainActivity extends AppCompatActivity {
    private List<Producto> listProduct = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProducto;
    EditText nombre, descripcion, precio;
    ListView list_productos;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Producto productoSelected;
    Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = findViewById(R.id.txt_nombre);
        descripcion = findViewById(R.id.txt_descripcion);
        precio = findViewById(R.id.txt_precio);

        list_productos = findViewById(R.id.list_datos);
        inicializarFirebase();
        //listarDatos();

        list_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                productoSelected = (Producto) adapterView.getItemAtPosition(position);
                nombre.setText(productoSelected.getNombre());
                descripcion.setText(productoSelected.getDescripcion());
                precio.setText(productoSelected.getPrecio());
                myMenu.findItem(R.id.icon_add).setVisible(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProduct.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Producto p = objSnapshot.getValue(Producto.class);
                    listProduct.add(p);
                }
                arrayAdapterProducto = new ArrayAdapter<Producto>(MainActivity.this, android.R.layout.simple_list_item_1, listProduct);
                list_productos.setAdapter(arrayAdapterProducto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        myMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombreP = nombre.getText().toString();
        String descripcionP = descripcion.getText().toString();
        String precioP = precio.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{
                if(nombreP.equals("")||descripcionP.equals("")||precioP.equals("")){
                    validation();
                }else{
                    String idFire = databaseReference.push().getKey();
                    Producto p = new Producto();
                    p.setId(idFire);
                    p.setNombre(nombreP);
                    p.setDescripcion(descripcionP);
                    p.setPrecio(precioP);
                    databaseReference.child("Producto").child(p.getId()).setValue(p);
                    Toast.makeText(this,"Agregar", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                break;
            }
            case R.id.icon_save:{
                if(nombreP.equals("")||descripcionP.equals("")||precioP.equals("")){
                    validation();
                }else {
                    Producto p = new Producto();
                    p.setId(productoSelected.getId());
                    p.setNombre(nombre.getText().toString().trim());
                    p.setDescripcion(descripcion.getText().toString().trim());
                    p.setPrecio(precio.getText().toString().trim());
                    databaseReference.child("Producto").child(p.getId()).setValue(p);
                    Toast.makeText(this, "Guardar", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                    myMenu.findItem(R.id.icon_add).setVisible(true);
                }
                break;
            }
            case R.id.icon_delete:{
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                databaseReference.child("Producto").child(p.getId()).removeValue();
                Toast.makeText(this,"Eliminar", Toast.LENGTH_LONG).show();
                limpiarCajas();
                myMenu.findItem(R.id.icon_add).setVisible(true);
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        nombre.setText("");
        descripcion.setText("");
        precio.setText("");
    }

    private void validation(){
        String nombreP = nombre.getText().toString();
        String descripcionP = descripcion.getText().toString();
        String precioP = precio.getText().toString();
        if(nombreP.equals("")) {
            nombre.setError("Requerido");
        } else if(descripcionP.equals("")) {
            descripcion.setError("Requerido");
        }else if(precioP.equals("")){
            precio.setError("Requerido");
        }
    }
}
