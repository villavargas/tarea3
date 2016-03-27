//
//  Datos.swift
//  Hamburguesas
//
//  Created by Luis Alejandro Villa Vargas on 27/03/16.
//  Copyright © 2016 Luis Alejandro Villa Vargas. All rights reserved.
//

import Foundation

class ColeccionDePaises {
    
    var paises: [String] = ["Mexico","Italia","EU","Rusia","Africa","Francia","Roma","Cuba","Chile",
                           "China","Haiti","India","Iran", "Peru", "Siuza","Sudan","Tunez","Francia",
                            "Corea","Egipto"]
    
    func obtenPais() ->String{
        let lugar = Int( arc4random()) % paises.count
        return paises [lugar]
    }
    
}

class ColeccionDeHamburguesa {
    var hamburguesas: [String]=["Hawaiana","Americana","Super","Quesos","Doble","Iberica","DelChef","Española","Barbacoa","Pueblo","Iberica","Trufa","Zetas","Foie","Tradicional","Pollo","Atun","Mini","Base","Samurai"]
    
    func obtenHamburguesa() ->String{
        let lugar = Int( arc4random()) % hamburguesas.count
        return hamburguesas [lugar]
    }
}
