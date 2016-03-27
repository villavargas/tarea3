//
//  ViewController.swift
//  Hamburguesas
//
//  Created by Luis Alejandro Villa Vargas on 27/03/16.
//  Copyright © 2016 Luis Alejandro Villa Vargas. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    
    @IBOutlet weak var mensajePaises: UILabel!
    
    
    @IBOutlet weak var mensajeHamburguesa: UILabel!
    
    let colores = Colores()
    
    let paises = ColeccionDePaises()
    
    let hamburguesas = ColeccionDeHamburguesa()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

  
  
    @IBAction func dameUnaHamburguesa() {
      mensajeHamburguesa.text = paises.obtenPais()
      mensajePaises.text = hamburguesas.obtenHamburguesa()
      let colorAleatorio = colores.regresaColorAleatorio()
      view.backgroundColor = colorAleatorio
      view.tintColor = colorAleatorio
    }
}

