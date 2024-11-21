# Telcel B2C 

## Requerimientos

- Sap Commerce 2105 patch 3
- Pack Telco 2111 patch 0
- TUA Spartacus 3.1 

## Instalación de ambiente local

Manual de instalación del ambiente local: [Configuración-Spartacus-Commerce.pdf](https://neoris0.sharepoint.com/:b:/r/sites/PROYECTO_TELCEL_B2C/Documentos%20compartidos/General/B2C%20TIENDA%20EN%20LINEA/Instaladores/Instalacion%20local%20Telcel%20B2C/telcel-b2c-Instalacion-local.pdf?csf=1&web=1&e=hFVvTB)
> Se requiere tener acceso al grupo de Teams PROYECTO_TELCEL_B2C

## Clone del repositorio 

Clonar repositorio con:
``` 
git clone git@gitlab.newsite.telcel.com:DevOps/sap-commerce-spartacus.git
```

#### SAP Commerce


#### Spartacus

1. Abrir el Node prompt en el path: <sap-commerce-spartacus>/js-storefront/tuaspastore
2. Ejecutar para instalar dependencias:
```
yarn install
```
3. Levantar la storefront:
```
yarn start --ssl
```

## Gitflow

## Consideraciones del control de código

### License
Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
