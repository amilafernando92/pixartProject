var contatoreRigheInput = 0;

function addToInput() {
	if ($('#inpuText').val().length > 0) {
		let app = '<tr id="riga_' + contatoreRigheInput + '" class="rowClass">';
		app += '<td title="Stringa in INPUT" class="valueInput width90_">"' + $('#inpuText').val().replaceAll(/,/g,'') + '"</td>';
		app += '<td title="Modifica della stringa \'' + $('#inpuText').val() + '\'" class="editInput width5_"><a onclick="editInput(this)"><svg class="width20 height20" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M402.6 83.2l90.2 90.2c3.8 3.8 3.8 10 0 13.8L274.4 405.6l-92.8 10.3c-12.4 1.4-22.9-9.1-21.5-21.5l10.3-92.8L388.8 83.2c3.8-3.8 10-3.8 13.8 0zm162-22.9l-48.8-48.8c-15.2-15.2-39.9-15.2-55.2 0l-35.4 35.4c-3.8 3.8-3.8 10 0 13.8l90.2 90.2c3.8 3.8 10 3.8 13.8 0l35.4-35.4c15.2-15.3 15.2-40 0-55.2zM384 346.2V448H64V128h229.8c3.2 0 6.2-1.3 8.5-3.5l40-40c7.6-7.6 2.2-20.5-8.5-20.5H48C21.5 64 0 85.5 0 112v352c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48V306.2c0-10.7-12.9-16-20.5-8.5l-40 40c-2.2 2.3-3.5 5.3-3.5 8.5z"/></svg></a></td>';	//modifica
		app += '<td title="Elimina della stringa \'' + $('#inpuText').val() + '\'" class="deleteInput width5_"><a onclick="removeTr(this)"><svg class="width20 height20" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><path d="M32 464a48 48 0 0 0 48 48h288a48 48 0 0 0 48-48V128H32zm272-256a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zm-96 0a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zm-96 0a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zM432 32H312l-9.4-18.7A24 24 0 0 0 281.1 0H166.8a23.72 23.72 0 0 0-21.4 13.3L136 32H16A16 16 0 0 0 0 48v32a16 16 0 0 0 16 16h416a16 16 0 0 0 16-16V48a16 16 0 0 0-16-16z"/></svg></a></td>';	//elimina
		app += '</tr>';
		contatoreRigheInput++;
    	$('#lista_input tbody').append(app);
    	$('#inpuText').val('');
		$('#inpuText').removeClass('has_value');
	} else {
		$('#inpuText').closest('div').addClass('has-error');
	}
}

function removeTr(element) {
	let tr_id = $(element).closest('tr').attr('id');
	$('#lista_input tbody tr#' + tr_id).remove();
}

function editInput(element) {
	let tr_id = $(element).closest('tr').attr('id');
	let value = $('#lista_input tbody tr#' + tr_id).find('.valueInput').text();
	value = value.substring(1, value.length-1);
	$('#stringModifica').val(value);
	$('#idTR').val(tr_id);
	checkInput('#stringModifica');
	$('#modalModifica').modal('show');
}

function closeModal(element) {
	let modalId = $(element).closest('div.classModal.modal').attr('id');
	$('#' + modalId).modal('hide');
}

function transformaListaBrackets() {
	let arrayInput = [];
	if($('#lista_input tbody').text().length > 0) {
		$('#lista_input tbody tr').find('td.valueInput').each(function(){
			let value = $(this).text();
			value = value.substring(1, value.length-1);
			arrayInput.push(value);
		});
		$.ajax({
		    type: 'POST',
		    url: 'pixartServlet/',
		    data: 'MAP=BRACKETS&ARRAYINPUT=' + arrayInput,    
		    success: function(risposta) {
				let app = '';
				$.each(risposta, function(i, stringa) {
					app += '<tr class="rowClass">';
					app += '<td title="Stringa in OUTPUT" class="valueOutput width100_">"' + stringa + '"</td>';
					app += '</tr>';
				});
		    	$('#lista_output tbody').empty();
		    	$('#lista_output tbody').append(app);
		    },
		    error: function(errore) {
		    }
		});
	} else {
    	$('#lista_output tbody').empty();
		$('#modalErrore').modal('show');
	}
}

function transformaListaPairsEn() {
	let arrayInput = [];
	if($('#lista_input tbody').text().length > 0) {
		$('#lista_input tbody tr').find('td.valueInput').each(function(){
			let value = $(this).text();
			value = value.substring(1, value.length-1);
			arrayInput.push(value);
		});
		$.ajax({
		    type: 'POST',
		    url: 'pixartServlet/',
		    data: 'MAP=PAIRSEN&ARRAYINPUT=' + arrayInput,    
		    success: function(risposta) {
				let app = '';
				$.each(risposta, function(i, stringa) {
					app += '<tr class="rowClass">';
					app += '<td title="Stringa in OUTPUT" class="valueOutput width100_">"' + stringa + '"</td>';
					app += '</tr>';
				});
		    	$('#lista_output tbody').empty();
		    	$('#lista_output tbody').append(app);
		    },
		    error: function(errore) {
		    }
		});
	} else {
    	$('#lista_output tbody').empty();
		$('#modalErrore').modal('show');
	}
}

function confermaModifica() {
	if (validator()) {
		$('#lista_input tbody tr#' + $('#idTR').val()).find('.valueInput').text('"' + $('#stringModifica').val().replaceAll(/,/g,'') + '"');
		$('#modalModifica').modal('hide');
	}
}

function validator() {
	if ($('#formModifica input#stringModifica').val() == null || $('#formModifica input#stringModifica').val() == '' || $('#formModifica input#stringModifica').val().length < 0) {
		$('#formModifica input#stringModifica').closest('div.custom_input_container').addClass('has-error');
	} else {
		$('#formModifica input#stringModifica').closest('div.custom_input_container').removeClass('has-error');
	}
	var res = true;
	if ($('#formModifica div.has-error').length > 0) {
		res = false;
	}
	return res;
}

function checkInput(element) {
	if ($(element).val().length > 0) {
		$(element).addClass('has_value');
		$(element).closest('div').removeClass('has-error');
	} else {
		$(element).removeClass('has_value');
	}
}

function generaPDF(element) {
	let arrayInput = [];
	$('#lista_input tbody tr').find('td.valueInput').each(function(){
		let value = $(this).text();
		value = value.substring(1, value.length-1);
		arrayInput.push(value);
	});
	let arrayOutput = [];
	$('#lista_output tbody tr').find('td.valueOutput').each(function(){
		let value = $(this).text();
		value = value.substring(1, value.length-1);
		arrayOutput.push(value);
	});
	$.ajax({
	    type: 'POST',
	    url: 'pixartServlet/',
	    data: 'MAP=PDF&ARRAYINPUT=' + arrayInput + '&ARRAYOUTPUT=' + arrayOutput,    
	    success: function(risposta) {
			let variable = risposta.replaceAll('\\', '#');
			window.open('downloadPDF.html?URL=' + variable, '_self');
	    },
	    error: function(errore) {
	    }
	});
}

function urlParameter(parameter) {
    let urlPage = window.location.href.split('?')[1];
    let urlVariables = urlPage.split('&');
    for (let cont=0;cont<urlVariables.length;cont++) {
        let nomeParameter = urlVariables[cont].split('=');
        if (nomeParameter[0] == parameter) {
            return nomeParameter[1];
        }
    }
}

function downloadPDF() {
	if ($('#idURL').val() == null || $('#idURL').val() == '' || $('#idURL').val().length < 0) {
		console.log('errore');
		$('#errorP').text('ATTENZIONE!! FILE PDF INESISTENTE');
		$('#modalErrore').modal('show');
	} else {
		$('#formDownload').submit();
		window.open('index.html', '_self');
	}
}