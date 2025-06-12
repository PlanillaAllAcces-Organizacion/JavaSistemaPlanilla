-- Crear base de datos
CREATE DATABASE Paysheet2;
GO

-- Usar la base de datos
USE Paysheet2;
GO


CREATE TABLE Usuarios (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL,
    password VARCHAR(64) NOT NULL,
    fechaCreacion DATETIME NOT NULL,
    status TINYINT NOT NULL
);

-- Tabla: PuestoTrabajo
CREATE TABLE PuestoTrabajo (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    NombrePuesto VARCHAR(50) NOT NULL,
    SalarioBase DECIMAL(10,2) NOT NULL,
    ValorxHora DECIMAL(8,2) NOT NULL,
    ValorExtra DECIMAL(8,2) NOT NULL,
    Estado TINYINT NULL
);
GO

-- Tabla: TipoDeHorario
CREATE TABLE TipoDeHorario (
   Id INT IDENTITY(1,1) PRIMARY KEY,
   NombreHorario VARCHAR(50) NOT NULL
);
GO

-- Tabla: Empleado
CREATE TABLE Empleado (
   Id INT IDENTITY(1,1) PRIMARY KEY,
   TipoDeHorarioId INT NULL,
   PuestoTrabajoId INT NULL,
   DUI VARCHAR(10) NOT NULL,
   Nombre VARCHAR(50) NOT NULL,
   Apellido VARCHAR(50) NOT NULL,
   Telefono INT NOT NULL,
   Correo VARCHAR(100) NULL,
   Estado TINYINT NULL,
   SalarioBase DECIMAL(10,2) NULL,
   FechaContraInicial DATETIME NOT NULL,
   CONSTRAINT FK_Empleado_Horario FOREIGN KEY (TipoDeHorarioId) REFERENCES TipoDeHorario(Id),
   CONSTRAINT FK_Empleado_Puesto FOREIGN KEY (PuestoTrabajoId) REFERENCES PuestoTrabajo(Id)
);
GO

-- Tabla: Descuento
CREATE TABLE Descuento (
   Id INT IDENTITY(1,1) PRIMARY KEY,
   Nombre VARCHAR(50) NOT NULL,
   Valor DECIMAL(8,2) NOT NULL,
   Estado TINYINT NULL,
   Operacion TINYINT NOT NULL,
   Planilla TINYINT NOT NULL
);
GO

-- Tabla: AsignacionDescuento
CREATE TABLE AsignacionDescuento (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    EmpleadosId INT NOT NULL,
    DescuentosId INT NOT NULL,
    CONSTRAINT FK_AsignacionDescuento_Empleado FOREIGN KEY (EmpleadosId) REFERENCES Empleado(Id),
    CONSTRAINT FK_AsignacionDescuento_Descuento FOREIGN KEY (DescuentosId) REFERENCES Descuento(Id)
);
GO

-- Tabla: Bono
CREATE TABLE Bono (
     Id INT IDENTITY(1,1) PRIMARY KEY,
     NombreBono VARCHAR(50) NOT NULL,
     Valor DECIMAL(8,2) NOT NULL,
     Estado TINYINT NULL,
     Operacion TINYINT NOT NULL,
     Planilla TINYINT NOT NULL
);
GO

-- Tabla: AsignacionBono
CREATE TABLE AsignacionBono (
     Id INT IDENTITY(1,1) PRIMARY KEY,
     EmpleadosId INT NOT NULL,
     BonosId INT NOT NULL,
     Estado TINYINT NOT NULL,
     CONSTRAINT FK_AsignacionBono_Empleado FOREIGN KEY (EmpleadosId) REFERENCES Empleado(Id),
     CONSTRAINT FK_AsignacionBono_Bono FOREIGN KEY (BonosId) REFERENCES Bono(Id)
);
GO

-- Tabla: PagoEmpleado
CREATE TABLE PagoEmpleado (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    EmpleadoId INT NOT NULL,
    FechaPago DATE NOT NULL,
    HorasTrabajadas INT NOT NULL,
    ValorHora DECIMAL(8,2) NOT NULL,
    TotalPago INT NOT NULL,
    CONSTRAINT FK_PagoEmpleado_Empleado FOREIGN KEY (EmpleadoId) REFERENCES Empleado(Id)
);

GO

-- Eliminar la columna 'Planilla' de la tabla Descuento
ALTER TABLE Descuento
DROP COLUMN Planilla;
GO

-- Eliminar la columna 'Planilla' de la tabla Bono
ALTER TABLE Bono
DROP COLUMN Planilla;
GO

-- Eliminar la columna Estado de la tabla AsignacionBono
ALTER TABLE AsignacionBono
DROP COLUMN Estado;
GO