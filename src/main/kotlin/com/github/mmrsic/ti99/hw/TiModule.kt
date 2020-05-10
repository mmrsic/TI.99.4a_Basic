package com.github.mmrsic.ti99.hw

interface TiModule {
   /**Enter this module via master computer title screen. */
   fun enter()

   /** Leave this module and return to master computer title screen.*/
   fun leave()
}