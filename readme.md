# 🍽️ Sistema de Gestión de Restaurante

Este es un proyecto en **Java** con conexión a base de datos, diseñado para la gestión de un restaurante.  
El sistema permite administrar clientes, empleados, pedidos y más, mediante una interfaz gráfica amigable.

---

## 📂 Estructura del Proyecto

```
restaurante/
├── src/
│ ├── com.mycompany.restaurante/
│ │ ├── controllers/
│ │ │ ├── ClienteController.java
│ │ │ ├── EmpleadoController.java
│ │ │ ├── PedidoController.java
│ │ │ ├── PlatilloController.java
│ │ │ └── ProveedorController.java
│ │ ├── database/
│ │ │ └── DatabaseConnection.java
│ │ ├── models/
│ │ │ ├── Cliente.java
│ │ │ ├── Empleado.java
│ │ │ ├── Pedido.java
│ │ │ ├── Platillo.java
│ │ │ └── Proveedor.java
│ │ ├── views/
│ │ │ ├── ClienteView.java
│ │ │ ├── EmpleadoView.java
│ │ │ ├── MainView.java
│ │ │ ├── PedidoView.java
│ │ │ ├── PlatilloView.java
│ │ │ └── ProveedorView.java
│ │ └── App.java
├── README.md
├── pom.xml
└── Base de datos/
    └── restaurante.sql
```

---

## ⚙️ Requisitos

- **Java 17** o superior
- **Maven** para gestión de dependencias
- **MySQL** (o el SGBD configurado en `DatabaseConnection.java`)
- IDE recomendado: **NetBeans** o **IntelliJ IDEA**

---

## 🚀 Instalación y Ejecución

1. **Clonar el repositorio**  
   ```bash
   git clone https://github.com/Inu41123/ProyectoSistemaRestaurante.git
   ```

---
## Configurar la base de datos

**1.- Importar** el archivo restaurante.sql en tu gestor de base de datos.

**2.- Actualizar** las credenciales en DatabaseConnection.java.

**3.- Compilar y ejecutar el proyecto**