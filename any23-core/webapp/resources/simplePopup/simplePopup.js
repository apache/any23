/*
** Simple Popup
** PhPixel
** http://www.phpixel.fr/simple-popup/
**
** v 1.4 :
** - Correction d'un problème d'affichage
** v 1.3 :
** - Correction d'un problème d'affichage
** v 1.2 :
** - Correction d'un problème d'affichage
** - Remplacement du lien texte de fermeture par une version image
** - La vitesse de l'effet fade est désormais paramétrable
** v 1.1 :
** - La fonction sPopup requiert désormais un objet comme parametre
*/
function sPopup(param){
	$(document).ready(function(){
		// on applique un ID aux elements classe sPopup-content
		// l'ID correspond aux attributs rel des elements classe sPopup
		$(".sPopup").each(function(i){
			var id_popup = '#'+$(this).attr('rel');
			$(id_popup).addClass('sPopup-content');
		});
		// au click...
		$(".sPopup").click(function(){
			// on recupere la position de l'element cliqué (gestion du scroll)
			pos = Math.round($(this).position().top);
			// on recupere l'ID de la popup a ouvrir dans l'attribut rel de l'element clique
			var id_popup = '#'+$(this).attr('rel');
			// on recupere le contenu de la popup
			var contenu = $(id_popup).html();
			// on affiche
			$('body').append('<div id="sPopup-container"><div style="width:'+param.width+'px;" id="sPopup-popup"><div title="'+param.closeTexte+'" id="sPopup-close"></div><div style="clear:both;"></div>'+contenu+'</div></div>');
			$('body').css({ overflow : "hidden" });
			window.scrollTo(0, 0);
			// on gere la fermeture de la popup
			$("#sPopup-close").click(function(){
				$("#sPopup-container").fadeOut(param.fadeSpeed, function(){
					$(this).remove();
					//window.scrollTo(0, pos);
					$('body').css({ overflow : "auto" });
				});
			});
		});
	});
}