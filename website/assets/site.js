document.querySelectorAll('[data-copy]').forEach(button => {
  button.addEventListener('click', async () => {
    const code = document.getElementById(button.dataset.copy);
    const status = button.closest('.code-panel').querySelector('.copy-status');
    try {
      await navigator.clipboard.writeText(code.textContent);
      button.textContent = 'Copied';
      status.textContent = 'Commands copied. Replace <tv-ip-address> with the address shown on your TV.';
    } catch {
      status.textContent = 'Select the commands in the code block and copy them manually.';
    }
  });
});
