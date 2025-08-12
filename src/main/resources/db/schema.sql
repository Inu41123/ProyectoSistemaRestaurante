CREATE TABLE Clientes (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Platos (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0)
);

CREATE TABLE Pedidos (
    id INT PRIMARY KEY IDENTITY(1,1),
    cliente_id INT NOT NULL,
    fecha DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (cliente_id) REFERENCES Clientes(id) ON DELETE CASCADE
);

CREATE TABLE Pedido_Platos (
    pedido_id INT NOT NULL,
    plato_id INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    PRIMARY KEY (pedido_id, plato_id),
    FOREIGN KEY (pedido_id) REFERENCES Pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (plato_id) REFERENCES Platos(id) ON DELETE CASCADE
);