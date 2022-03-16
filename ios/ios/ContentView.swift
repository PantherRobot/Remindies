//
//  ContentView.swift
//  ios
//
//  Created by Sergey V. on 16.03.2022.
//

import SwiftUI
import Remindies

struct ContentView: View {
    var body: some View {
        Text(Greeting().greeting())
            .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
